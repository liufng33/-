import { PlaybackViewModel } from '../PlaybackViewModel';
import { FetchPlaybackStreamsUseCase } from '../../../domain/use-cases/fetch-playback/FetchPlaybackStreamsUseCase';
import { IPlaybackRepository } from '../../../domain/repositories/IPlaybackRepository';
import { VideoItem } from '../../../domain/entities/VideoItem';
import { PlaybackLink, PlaybackFormat } from '../../../domain/entities/PlaybackLink';
import { URL } from '../../../domain/value-objects/URL';
import { Quality, QualityLevel } from '../../../domain/value-objects/Quality';

describe('PlaybackViewModel', () => {
  let viewModel: PlaybackViewModel;
  let mockPlaybackRepo: jest.Mocked<IPlaybackRepository>;
  let fetchPlaybackUseCase: FetchPlaybackStreamsUseCase;

  const createMockVideo = (): VideoItem => {
    return new VideoItem({
      id: 'video-1',
      title: 'Test Video',
      url: new URL('https://example.com/video'),
      sourceId: 'test-source',
    });
  };

  const createMockPlaybackLink = (resolution: string): PlaybackLink => {
    return new PlaybackLink({
      id: `link-${resolution}`,
      url: new URL(`https://example.com/stream-${resolution}`),
      quality: new Quality(QualityLevel.HIGH, resolution),
      format: PlaybackFormat.MP4,
      videoId: 'video-1',
    });
  };

  beforeEach(() => {
    mockPlaybackRepo = {
      getPlaybackLinks: jest.fn(),
      getPlaybackLink: jest.fn(),
      refreshPlaybackLink: jest.fn(),
    };

    fetchPlaybackUseCase = new FetchPlaybackStreamsUseCase(mockPlaybackRepo);
    viewModel = new PlaybackViewModel(fetchPlaybackUseCase);
  });

  describe('loadPlaybackLinks', () => {
    it('should load playback links successfully', async () => {
      const mockVideo = createMockVideo();
      const mockLinks = [
        createMockPlaybackLink('1080p'),
        createMockPlaybackLink('720p'),
        createMockPlaybackLink('480p'),
      ];

      mockPlaybackRepo.getPlaybackLinks.mockResolvedValue(mockLinks);

      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      await viewModel.loadPlaybackLinks(mockVideo);

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          video: mockVideo,
          playbackLinks: mockLinks,
          selectedLink: mockLinks[0],
          isLoading: false,
          error: null,
        })
      );
    });

    it('should select best quality link automatically', async () => {
      const mockVideo = createMockVideo();
      const mockLinks = [
        createMockPlaybackLink('480p'),
        createMockPlaybackLink('1080p'),
        createMockPlaybackLink('720p'),
      ];

      mockPlaybackRepo.getPlaybackLinks.mockResolvedValue(mockLinks);

      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      await viewModel.loadPlaybackLinks(mockVideo);

      const state = viewModel.getState();
      expect(state.selectedLink?.getQuality().getResolution()).toBe('1080p');
    });

    it('should handle errors when loading playback links', async () => {
      const mockVideo = createMockVideo();
      mockPlaybackRepo.getPlaybackLinks.mockRejectedValue(new Error('Failed to load'));

      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      await viewModel.loadPlaybackLinks(mockVideo);

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          isLoading: false,
          error: 'Failed to load',
        })
      );
    });

    it('should handle empty playback links', async () => {
      const mockVideo = createMockVideo();
      mockPlaybackRepo.getPlaybackLinks.mockResolvedValue([]);

      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      await viewModel.loadPlaybackLinks(mockVideo);

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          playbackLinks: [],
          selectedLink: null,
        })
      );
    });
  });

  describe('selectPlaybackLink', () => {
    it('should update selected link', async () => {
      const mockVideo = createMockVideo();
      const mockLinks = [
        createMockPlaybackLink('1080p'),
        createMockPlaybackLink('720p'),
      ];

      mockPlaybackRepo.getPlaybackLinks.mockResolvedValue(mockLinks);

      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      await viewModel.loadPlaybackLinks(mockVideo);
      viewModel.selectPlaybackLink(mockLinks[1]);

      expect(listener).toHaveBeenLastCalledWith(
        expect.objectContaining({
          selectedLink: mockLinks[1],
        })
      );
    });
  });

  describe('clearState', () => {
    it('should clear playback state', () => {
      const listener = jest.fn();
      viewModel.subscribeToState(listener);

      viewModel.clearState();

      expect(listener).toHaveBeenCalledWith(
        expect.objectContaining({
          video: null,
          playbackLinks: [],
          selectedLink: null,
          error: null,
        })
      );
    });
  });
});
