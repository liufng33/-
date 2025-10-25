import { ParsePastedUrlUseCase } from './ParsePastedUrlUseCase';
import { IParserSourceRepository } from '../../repositories/IParserSourceRepository';
import { ParserConfig } from '../../entities/ParserConfig';
import { VideoItem } from '../../entities/VideoItem';
import { URL } from '../../value-objects/URL';

describe('ParsePastedUrlUseCase', () => {
  let useCase: ParsePastedUrlUseCase;
  let mockRepository: jest.Mocked<IParserSourceRepository>;

  beforeEach(() => {
    mockRepository = {
      findParserForUrl: jest.fn(),
      getAllParsers: jest.fn(),
      getActiveParsers: jest.fn(),
      parseVideoPage: jest.fn()
    };

    useCase = new ParsePastedUrlUseCase(mockRepository);
  });

  describe('execute', () => {
    it('should throw error if URL is empty', async () => {
      await expect(useCase.execute({ url: '' })).rejects.toThrow('URL cannot be empty');
    });

    it('should throw error if URL is invalid', async () => {
      await expect(useCase.execute({ url: 'not-a-valid-url' })).rejects.toThrow('Invalid URL');
    });

    it('should return null if no parser is found for URL', async () => {
      mockRepository.findParserForUrl.mockResolvedValue(null);

      const result = await useCase.execute({ url: 'https://example.com/video' });

      expect(result).toBeNull();
      expect(mockRepository.findParserForUrl).toHaveBeenCalledWith('https://example.com/video');
    });

    it('should throw error if parser is disabled', async () => {
      const parser = new ParserConfig({
        id: 'parser-1',
        name: 'Test Parser',
        urlPattern: '.*example\\.com.*',
        rules: [],
        enabled: false
      });

      mockRepository.findParserForUrl.mockResolvedValue(parser);

      await expect(useCase.execute({ url: 'https://example.com/video' }))
        .rejects.toThrow('Parser is disabled: Test Parser');
    });

    it('should successfully parse URL and return video item', async () => {
      const parser = new ParserConfig({
        id: 'parser-1',
        name: 'Test Parser',
        urlPattern: '.*example\\.com.*',
        rules: [],
        enabled: true
      });

      const videoItem = new VideoItem({
        id: 'video-1',
        title: 'Test Video',
        url: new URL('https://example.com/video'),
        sourceId: 'parser-1'
      });

      mockRepository.findParserForUrl.mockResolvedValue(parser);
      mockRepository.parseVideoPage.mockResolvedValue(videoItem);

      const result = await useCase.execute({ url: 'https://example.com/video' });

      expect(result).toEqual(videoItem);
      expect(mockRepository.parseVideoPage).toHaveBeenCalledWith(parser, 'https://example.com/video');
    });

    it('should trim whitespace from URL', async () => {
      const parser = new ParserConfig({
        id: 'parser-1',
        name: 'Test Parser',
        urlPattern: '.*example\\.com.*',
        rules: [],
        enabled: true
      });

      const videoItem = new VideoItem({
        id: 'video-1',
        title: 'Test Video',
        url: new URL('https://example.com/video'),
        sourceId: 'parser-1'
      });

      mockRepository.findParserForUrl.mockResolvedValue(parser);
      mockRepository.parseVideoPage.mockResolvedValue(videoItem);

      const result = await useCase.execute({ url: '  https://example.com/video  ' });

      expect(result).toEqual(videoItem);
      expect(mockRepository.findParserForUrl).toHaveBeenCalledWith('https://example.com/video');
    });

    it('should return null if parsing fails', async () => {
      const parser = new ParserConfig({
        id: 'parser-1',
        name: 'Test Parser',
        urlPattern: '.*example\\.com.*',
        rules: [],
        enabled: true
      });

      mockRepository.findParserForUrl.mockResolvedValue(parser);
      mockRepository.parseVideoPage.mockResolvedValue(null);

      const result = await useCase.execute({ url: 'https://example.com/video' });

      expect(result).toBeNull();
    });
  });
});
