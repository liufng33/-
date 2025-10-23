import { SearchVideosUseCase, SearchVideosRequest } from '../../domain/use-cases/search-videos/SearchVideosUseCase';
import { ListSourcesUseCase, ListSourcesRequest } from '../../domain/use-cases/manage-sources/ListSourcesUseCase';
import { VideoItem } from '../../domain/entities/VideoItem';
import { SourceConfig } from '../../domain/entities/SourceConfig';
import { SourceTypeEnum } from '../../domain/value-objects/SourceType';

export interface SearchState {
  videos: VideoItem[];
  isLoading: boolean;
  error: string | null;
  total: number;
  hasMore: boolean;
  currentPage: number;
  query: string;
  selectedSourceId: string | null;
}

export interface SourcesState {
  sources: SourceConfig[];
  isLoading: boolean;
  error: string | null;
}

export class SearchViewModel {
  private searchUseCase: SearchVideosUseCase;
  private listSourcesUseCase: ListSourcesUseCase;
  private searchStateListeners: ((state: SearchState) => void)[] = [];
  private sourcesStateListeners: ((state: SourcesState) => void)[] = [];
  
  private searchState: SearchState = {
    videos: [],
    isLoading: false,
    error: null,
    total: 0,
    hasMore: false,
    currentPage: 0,
    query: '',
    selectedSourceId: null,
  };

  private sourcesState: SourcesState = {
    sources: [],
    isLoading: false,
    error: null,
  };

  private readonly PAGE_SIZE = 20;

  constructor(
    searchUseCase: SearchVideosUseCase,
    listSourcesUseCase: ListSourcesUseCase
  ) {
    this.searchUseCase = searchUseCase;
    this.listSourcesUseCase = listSourcesUseCase;
  }

  subscribeToSearchState(listener: (state: SearchState) => void): () => void {
    this.searchStateListeners.push(listener);
    listener(this.searchState);
    
    return () => {
      this.searchStateListeners = this.searchStateListeners.filter(l => l !== listener);
    };
  }

  subscribeToSourcesState(listener: (state: SourcesState) => void): () => void {
    this.sourcesStateListeners.push(listener);
    listener(this.sourcesState);
    
    return () => {
      this.sourcesStateListeners = this.sourcesStateListeners.filter(l => l !== listener);
    };
  }

  private notifySearchStateChanged(): void {
    this.searchStateListeners.forEach(listener => listener(this.searchState));
  }

  private notifySourcesStateChanged(): void {
    this.sourcesStateListeners.forEach(listener => listener(this.sourcesState));
  }

  async loadSources(): Promise<void> {
    this.sourcesState = {
      ...this.sourcesState,
      isLoading: true,
      error: null,
    };
    this.notifySourcesStateChanged();

    try {
      const request: ListSourcesRequest = {
        type: SourceTypeEnum.SEARCH,
        enabledOnly: true,
      };
      const sources = await this.listSourcesUseCase.execute(request);
      
      this.sourcesState = {
        sources,
        isLoading: false,
        error: null,
      };
      this.notifySourcesStateChanged();
    } catch (error) {
      this.sourcesState = {
        ...this.sourcesState,
        isLoading: false,
        error: error instanceof Error ? error.message : 'Failed to load sources',
      };
      this.notifySourcesStateChanged();
    }
  }

  async search(query: string, sourceId?: string): Promise<void> {
    if (!query || query.trim().length === 0) {
      this.searchState = {
        videos: [],
        isLoading: false,
        error: null,
        total: 0,
        hasMore: false,
        currentPage: 0,
        query: '',
        selectedSourceId: sourceId || null,
      };
      this.notifySearchStateChanged();
      return;
    }

    this.searchState = {
      ...this.searchState,
      isLoading: true,
      error: null,
      query,
      selectedSourceId: sourceId || null,
      currentPage: 0,
    };
    this.notifySearchStateChanged();

    try {
      const request: SearchVideosRequest = {
        query: query.trim(),
        sourceId,
        limit: this.PAGE_SIZE,
        offset: 0,
      };

      const result = await this.searchUseCase.execute(request);
      
      this.searchState = {
        videos: result.items,
        isLoading: false,
        error: null,
        total: result.total,
        hasMore: result.hasMore,
        currentPage: 1,
        query,
        selectedSourceId: sourceId || null,
      };
      this.notifySearchStateChanged();
    } catch (error) {
      this.searchState = {
        ...this.searchState,
        isLoading: false,
        error: error instanceof Error ? error.message : 'Search failed',
      };
      this.notifySearchStateChanged();
    }
  }

  async loadMore(): Promise<void> {
    if (this.searchState.isLoading || !this.searchState.hasMore) {
      return;
    }

    this.searchState = {
      ...this.searchState,
      isLoading: true,
    };
    this.notifySearchStateChanged();

    try {
      const request: SearchVideosRequest = {
        query: this.searchState.query,
        sourceId: this.searchState.selectedSourceId || undefined,
        limit: this.PAGE_SIZE,
        offset: this.searchState.currentPage * this.PAGE_SIZE,
      };

      const result = await this.searchUseCase.execute(request);
      
      this.searchState = {
        videos: [...this.searchState.videos, ...result.items],
        isLoading: false,
        error: null,
        total: result.total,
        hasMore: result.hasMore,
        currentPage: this.searchState.currentPage + 1,
        query: this.searchState.query,
        selectedSourceId: this.searchState.selectedSourceId,
      };
      this.notifySearchStateChanged();
    } catch (error) {
      this.searchState = {
        ...this.searchState,
        isLoading: false,
        error: error instanceof Error ? error.message : 'Failed to load more results',
      };
      this.notifySearchStateChanged();
    }
  }

  clearSearch(): void {
    this.searchState = {
      videos: [],
      isLoading: false,
      error: null,
      total: 0,
      hasMore: false,
      currentPage: 0,
      query: '',
      selectedSourceId: null,
    };
    this.notifySearchStateChanged();
  }

  getSearchState(): SearchState {
    return this.searchState;
  }

  getSourcesState(): SourcesState {
    return this.sourcesState;
  }
}
