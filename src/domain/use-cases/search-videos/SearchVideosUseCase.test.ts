import { SearchVideosUseCase } from './SearchVideosUseCase';
import { ISearchSourceRepository, SearchResult } from '../../repositories/ISearchSourceRepository';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceConfig } from '../../entities/SourceConfig';
import { VideoItem } from '../../entities/VideoItem';
import { SourceId } from '../../value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../../value-objects/SourceType';
import { URL } from '../../value-objects/URL';

describe('SearchVideosUseCase', () => {
  let useCase: SearchVideosUseCase;
  let mockSearchSourceRepository: jest.Mocked<ISearchSourceRepository>;
  let mockSourceConfigRepository: jest.Mocked<ISourceConfigRepository>;

  beforeEach(() => {
    mockSearchSourceRepository = {
      search: jest.fn(),
      getActiveSearchSources: jest.fn(),
      healthCheck: jest.fn()
    };

    mockSourceConfigRepository = {
      findById: jest.fn(),
      findAll: jest.fn(),
      findByType: jest.fn(),
      findEnabled: jest.fn(),
      save: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      exists: jest.fn()
    };

    useCase = new SearchVideosUseCase(mockSearchSourceRepository, mockSourceConfigRepository);
  });

  describe('execute', () => {
    it('should throw error if query is empty', async () => {
      await expect(useCase.execute({ query: '' })).rejects.toThrow('Search query cannot be empty');
    });

    it('should search using specific source when sourceId is provided', async () => {
      const sourceId = new SourceId('test-source');
      const source = new SourceConfig({
        id: sourceId,
        name: 'Test Source',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api.example.com'),
        enabled: true
      });

      const videoItem = new VideoItem({
        id: 'video-1',
        title: 'Test Video',
        url: new URL('https://example.com/video1'),
        sourceId: 'test-source'
      });

      const searchResult: SearchResult = {
        items: [videoItem],
        total: 1,
        hasMore: false
      };

      mockSourceConfigRepository.findById.mockResolvedValue(source);
      mockSearchSourceRepository.search.mockResolvedValue(searchResult);

      const result = await useCase.execute({ query: 'test', sourceId: 'test-source' });

      expect(result).toEqual(searchResult);
      expect(mockSourceConfigRepository.findById).toHaveBeenCalled();
      expect(mockSearchSourceRepository.search).toHaveBeenCalledWith(source, {
        query: 'test',
        limit: 20,
        offset: 0,
        filters: undefined
      });
    });

    it('should throw error if specified source is not found', async () => {
      mockSourceConfigRepository.findById.mockResolvedValue(null);

      await expect(useCase.execute({ query: 'test', sourceId: 'non-existent' }))
        .rejects.toThrow('Source not found: non-existent');
    });

    it('should throw error if specified source is disabled', async () => {
      const sourceId = new SourceId('test-source');
      const source = new SourceConfig({
        id: sourceId,
        name: 'Test Source',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api.example.com'),
        enabled: false
      });

      mockSourceConfigRepository.findById.mockResolvedValue(source);

      await expect(useCase.execute({ query: 'test', sourceId: 'test-source' }))
        .rejects.toThrow('Source is disabled: test-source');
    });

    it('should throw error if specified source is not a search source', async () => {
      const sourceId = new SourceId('test-source');
      const source = new SourceConfig({
        id: sourceId,
        name: 'Test Source',
        type: new SourceType(SourceTypeEnum.PARSER),
        apiUrl: new URL('https://api.example.com'),
        enabled: true
      });

      mockSourceConfigRepository.findById.mockResolvedValue(source);

      await expect(useCase.execute({ query: 'test', sourceId: 'test-source' }))
        .rejects.toThrow('Source is not a search source: test-source');
    });

    it('should search across all enabled sources when no sourceId is provided', async () => {
      const source1 = new SourceConfig({
        id: new SourceId('source-1'),
        name: 'Source 1',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api1.example.com'),
        enabled: true,
        priority: 10
      });

      const source2 = new SourceConfig({
        id: new SourceId('source-2'),
        name: 'Source 2',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api2.example.com'),
        enabled: true,
        priority: 5
      });

      const video1 = new VideoItem({
        id: 'video-1',
        title: 'Video 1',
        url: new URL('https://example.com/video1'),
        sourceId: 'source-1'
      });

      const video2 = new VideoItem({
        id: 'video-2',
        title: 'Video 2',
        url: new URL('https://example.com/video2'),
        sourceId: 'source-2'
      });

      mockSourceConfigRepository.findByType.mockResolvedValue([source1, source2]);
      mockSearchSourceRepository.search
        .mockResolvedValueOnce({ items: [video1], total: 1, hasMore: false })
        .mockResolvedValueOnce({ items: [video2], total: 1, hasMore: false });

      const result = await useCase.execute({ query: 'test' });

      expect(result.items).toHaveLength(2);
      expect(result.total).toBe(2);
      expect(mockSearchSourceRepository.search).toHaveBeenCalledTimes(2);
    });

    it('should return empty result when no enabled sources are found', async () => {
      mockSourceConfigRepository.findByType.mockResolvedValue([]);

      const result = await useCase.execute({ query: 'test' });

      expect(result).toEqual({
        items: [],
        total: 0,
        hasMore: false
      });
    });

    it('should handle search failures gracefully and return successful results', async () => {
      const source1 = new SourceConfig({
        id: new SourceId('source-1'),
        name: 'Source 1',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api1.example.com'),
        enabled: true
      });

      const source2 = new SourceConfig({
        id: new SourceId('source-2'),
        name: 'Source 2',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api2.example.com'),
        enabled: true
      });

      const video1 = new VideoItem({
        id: 'video-1',
        title: 'Video 1',
        url: new URL('https://example.com/video1'),
        sourceId: 'source-1'
      });

      mockSourceConfigRepository.findByType.mockResolvedValue([source1, source2]);
      mockSearchSourceRepository.search
        .mockResolvedValueOnce({ items: [video1], total: 1, hasMore: false })
        .mockRejectedValueOnce(new Error('Search failed'));

      const result = await useCase.execute({ query: 'test' });

      expect(result.items).toHaveLength(1);
      expect(result.items[0]).toEqual(video1);
    });

    it('should deduplicate videos from multiple sources', async () => {
      const source1 = new SourceConfig({
        id: new SourceId('source-1'),
        name: 'Source 1',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api1.example.com'),
        enabled: true
      });

      const source2 = new SourceConfig({
        id: new SourceId('source-2'),
        name: 'Source 2',
        type: new SourceType(SourceTypeEnum.SEARCH),
        apiUrl: new URL('https://api2.example.com'),
        enabled: true
      });

      const video1 = new VideoItem({
        id: 'video-1',
        title: 'Video 1',
        url: new URL('https://example.com/video1'),
        sourceId: 'source-1'
      });

      const video1Duplicate = new VideoItem({
        id: 'video-1',
        title: 'Video 1',
        url: new URL('https://example.com/video1'),
        sourceId: 'source-1'
      });

      mockSourceConfigRepository.findByType.mockResolvedValue([source1, source2]);
      mockSearchSourceRepository.search
        .mockResolvedValueOnce({ items: [video1], total: 1, hasMore: false })
        .mockResolvedValueOnce({ items: [video1Duplicate], total: 1, hasMore: false });

      const result = await useCase.execute({ query: 'test' });

      expect(result.items).toHaveLength(1);
    });
  });
});
