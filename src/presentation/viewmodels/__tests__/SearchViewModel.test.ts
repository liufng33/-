import { SearchViewModel } from '../SearchViewModel';
import { SearchVideosUseCase } from '../../../domain/use-cases/search-videos/SearchVideosUseCase';
import { ListSourcesUseCase } from '../../../domain/use-cases/manage-sources/ListSourcesUseCase';
import { ISearchSourceRepository } from '../../../domain/repositories/ISearchSourceRepository';
import { ISourceConfigRepository } from '../../../domain/repositories/ISourceConfigRepository';
import { VideoItem } from '../../../domain/entities/VideoItem';
import { SourceConfig } from '../../../domain/entities/SourceConfig';
import { SourceId } from '../../../domain/value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../../../domain/value-objects/SourceType';
import { URL } from '../../../domain/value-objects/URL';

describe('SearchViewModel', () => {
  let viewModel: SearchViewModel;
  let mockSearchRepo: jest.Mocked<ISearchSourceRepository>;
  let mockSourceRepo: jest.Mocked<ISourceConfigRepository>;
  let searchUseCase: SearchVideosUseCase;
  let listSourcesUseCase: ListSourcesUseCase;

  const createMockVideo = (id: string, title: string): VideoItem => {
    return new VideoItem({
      id,
      title,
      url: new URL('https://example.com/video'),
      sourceId: 'test-source',
    });
  };

  const createMockSource = (id: string, name: string): SourceConfig => {
    return new SourceConfig({
      id: new SourceId(id),
      name,
      type: new SourceType(SourceTypeEnum.SEARCH),
      apiUrl: new URL('https://api.example.com'),
      enabled: true,
    });
  };

  beforeEach(() => {
    mockSearchRepo = {
      search: jest.fn(),
      getActiveSearchSources: jest.fn(),
      healthCheck: jest.fn(),
    };

    mockSourceRepo = {
      findById: jest.fn(),
      findAll: jest.fn(),
      findByType: jest.fn(),
      findEnabled: jest.fn(),
      save: jest.fn(),
      delete: jest.fn(),
      update: jest.fn(),
      exists: jest.fn(),
    };

    searchUseCase = new SearchVideosUseCase(mockSearchRepo, mockSourceRepo);
    listSourcesUseCase = new ListSourcesUseCase(mockSourceRepo);
    viewModel = new SearchViewModel(searchUseCase, listSourcesUseCase);
  });

  describe('loadSources', () => {
    it('should load sources successfully', async () => {
      const mockSources = [
        createMockSource('source-1', 'Source 1'),
        createMockSource('source-2', 'Source 2'),
      ];

      mockSourceRepo.findByType.mockResolvedValue(mockSources);
      mockSourceRepo.findEnabled.mockResolvedValue(mockSources);

      const listener = jest.fn();
      viewModel.subscribeToSourcesState(listener);

      await viewModel.loadSources();

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          sources: mockSources,
          isLoading: false,
          error: null,
        })
      );
    });

    it('should handle errors when loading sources', async () => {
      mockSourceRepo.findEnabled.mockRejectedValue(new Error('Failed to load'));

      const listener = jest.fn();
      viewModel.subscribeToSourcesState(listener);

      await viewModel.loadSources();

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          isLoading: false,
          error: 'Failed to load',
        })
      );
    });
  });

  describe('search', () => {
    it('should search videos successfully', async () => {
      const mockVideos = [
        createMockVideo('1', 'Video 1'),
        createMockVideo('2', 'Video 2'),
      ];

      mockSourceRepo.findByType.mockResolvedValue([createMockSource('source-1', 'Source 1')]);
      mockSourceRepo.findEnabled.mockResolvedValue([createMockSource('source-1', 'Source 1')]);
      mockSearchRepo.search.mockResolvedValue({
        items: mockVideos,
        total: 2,
        hasMore: false,
      });

      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      await viewModel.search('test query');

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          videos: mockVideos,
          isLoading: false,
          error: null,
          query: 'test query',
          total: 2,
          hasMore: false,
        })
      );
    });

    it('should clear results for empty query', async () => {
      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      await viewModel.search('');

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          videos: [],
          query: '',
        })
      );
    });

    it('should handle search errors', async () => {
      mockSourceRepo.findByType.mockRejectedValue(new Error('Search failed'));

      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      await viewModel.search('test query');

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          isLoading: false,
          error: 'Search failed',
        })
      );
    });
  });

  describe('loadMore', () => {
    it('should load more videos', async () => {
      const initialVideos = [createMockVideo('1', 'Video 1')];
      const moreVideos = [createMockVideo('2', 'Video 2')];

      mockSourceRepo.findByType.mockResolvedValue([createMockSource('source-1', 'Source 1')]);
      mockSourceRepo.findEnabled.mockResolvedValue([createMockSource('source-1', 'Source 1')]);
      mockSearchRepo.search
        .mockResolvedValueOnce({
          items: initialVideos,
          total: 2,
          hasMore: true,
        })
        .mockResolvedValueOnce({
          items: moreVideos,
          total: 2,
          hasMore: false,
        });

      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      await viewModel.search('test query');
      await viewModel.loadMore();

      expect(listener).toHaveBeenLastCalledWith(
        expect.objectContaining({
          videos: [...initialVideos, ...moreVideos],
          hasMore: false,
        })
      );
    });

    it('should not load more if no more results', async () => {
      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      await viewModel.loadMore();

      expect(mockSearchRepo.search).not.toHaveBeenCalled();
    });
  });

  describe('clearSearch', () => {
    it('should clear search state', () => {
      const listener = jest.fn();
      viewModel.subscribeToSearchState(listener);

      viewModel.clearSearch();

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          videos: [],
          query: '',
          error: null,
        })
      );
    });
  });
});
