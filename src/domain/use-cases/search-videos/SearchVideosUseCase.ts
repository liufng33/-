import { ISearchSourceRepository, SearchOptions, SearchResult } from '../../repositories/ISearchSourceRepository';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceTypeEnum } from '../../value-objects/SourceType';
import { SourceId } from '../../value-objects/SourceId';
import { VideoItem } from '../../entities/VideoItem';

export interface SearchVideosRequest {
  query: string;
  sourceId?: string;
  limit?: number;
  offset?: number;
  filters?: Record<string, unknown>;
}

export class SearchVideosUseCase {
  constructor(
    private readonly searchSourceRepository: ISearchSourceRepository,
    private readonly sourceConfigRepository: ISourceConfigRepository
  ) {}

  async execute(request: SearchVideosRequest): Promise<SearchResult> {
    if (!request.query || request.query.trim().length === 0) {
      throw new Error('Search query cannot be empty');
    }

    const searchOptions: SearchOptions = {
      query: request.query.trim(),
      limit: request.limit ?? 20,
      offset: request.offset ?? 0,
      filters: request.filters
    };

    if (request.sourceId) {
      const sourceId = new SourceId(request.sourceId);
      const source = await this.sourceConfigRepository.findById(sourceId);

      if (!source) {
        throw new Error(`Source not found: ${request.sourceId}`);
      }

      if (!source.isEnabled()) {
        throw new Error(`Source is disabled: ${request.sourceId}`);
      }

      if (!source.isSearchSource()) {
        throw new Error(`Source is not a search source: ${request.sourceId}`);
      }

      return await this.searchSourceRepository.search(source, searchOptions);
    }

    const sources = await this.sourceConfigRepository.findByType(SourceTypeEnum.SEARCH);
    const enabledSources = sources.filter(s => s.isEnabled()).sort((a, b) => b.getPriority() - a.getPriority());

    if (enabledSources.length === 0) {
      return {
        items: [],
        total: 0,
        hasMore: false
      };
    }

    const results = await Promise.allSettled(
      enabledSources.map(source => this.searchSourceRepository.search(source, searchOptions))
    );

    const successfulResults = results
      .filter((result): result is PromiseFulfilledResult<SearchResult> => result.status === 'fulfilled')
      .map(result => result.value);

    if (successfulResults.length === 0) {
      return {
        items: [],
        total: 0,
        hasMore: false
      };
    }

    const allItems = successfulResults.flatMap(result => result.items);
    const uniqueItems = this.deduplicateVideos(allItems);
    const totalCount = successfulResults.reduce((sum, result) => sum + result.total, 0);

    return {
      items: uniqueItems,
      total: totalCount,
      hasMore: successfulResults.some(result => result.hasMore)
    };
  }

  private deduplicateVideos(items: VideoItem[]): VideoItem[] {
    const seen = new Set<string>();
    return items.filter(item => {
      const key = `${item.getSourceId()}-${item.getId()}`;
      if (seen.has(key)) {
        return false;
      }
      seen.add(key);
      return true;
    });
  }
}
