import { FetchPlaybackStreamsUseCase, FetchPlaybackStreamsRequest } from '../../domain/use-cases/fetch-playback/FetchPlaybackStreamsUseCase';
import { VideoItem } from '../../domain/entities/VideoItem';
import { PlaybackLink } from '../../domain/entities/PlaybackLink';

export interface PlaybackState {
  video: VideoItem | null;
  playbackLinks: PlaybackLink[];
  selectedLink: PlaybackLink | null;
  isLoading: boolean;
  error: string | null;
}

export class PlaybackViewModel {
  private fetchPlaybackUseCase: FetchPlaybackStreamsUseCase;
  private stateListeners: ((state: PlaybackState) => void)[] = [];
  
  private state: PlaybackState = {
    video: null,
    playbackLinks: [],
    selectedLink: null,
    isLoading: false,
    error: null,
  };

  constructor(fetchPlaybackUseCase: FetchPlaybackStreamsUseCase) {
    this.fetchPlaybackUseCase = fetchPlaybackUseCase;
  }

  subscribeToState(listener: (state: PlaybackState) => void): () => void {
    this.stateListeners.push(listener);
    listener(this.state);
    
    return () => {
      this.stateListeners = this.stateListeners.filter(l => l !== listener);
    };
  }

  private notifyStateChanged(): void {
    this.stateListeners.forEach(listener => listener(this.state));
  }

  async loadPlaybackLinks(video: VideoItem, refreshExpired: boolean = true): Promise<void> {
    this.state = {
      ...this.state,
      video,
      isLoading: true,
      error: null,
    };
    this.notifyStateChanged();

    try {
      const request: FetchPlaybackStreamsRequest = {
        video,
        refreshExpired,
      };

      const links = await this.fetchPlaybackUseCase.execute(request);
      
      const selectedLink = links.length > 0 ? this.selectBestQualityLink(links) : null;
      
      this.state = {
        video,
        playbackLinks: links,
        selectedLink,
        isLoading: false,
        error: null,
      };
      this.notifyStateChanged();
    } catch (error) {
      this.state = {
        ...this.state,
        isLoading: false,
        error: error instanceof Error ? error.message : 'Failed to load playback links',
      };
      this.notifyStateChanged();
    }
  }

  selectPlaybackLink(link: PlaybackLink): void {
    this.state = {
      ...this.state,
      selectedLink: link,
    };
    this.notifyStateChanged();
  }

  private selectBestQualityLink(links: PlaybackLink[]): PlaybackLink {
    const resolutionOrder = ['1080p', '720p', '480p', '360p', '240p', '144p'];
    
    for (const resolution of resolutionOrder) {
      const link = links.find(l => l.getQuality().getResolution() === resolution);
      if (link) {
        return link;
      }
    }
    
    return links[0];
  }

  getState(): PlaybackState {
    return this.state;
  }

  clearState(): void {
    this.state = {
      video: null,
      playbackLinks: [],
      selectedLink: null,
      isLoading: false,
      error: null,
    };
    this.notifyStateChanged();
  }
}
