import { PlaybackLink } from '../../entities/PlaybackLink';
import { VideoItem } from '../../entities/VideoItem';
import { IPlaybackRepository } from '../../repositories/IPlaybackRepository';

export interface FetchPlaybackStreamsRequest {
  video: VideoItem;
  refreshExpired?: boolean;
}

export class FetchPlaybackStreamsUseCase {
  constructor(private readonly playbackRepository: IPlaybackRepository) {}

  async execute(request: FetchPlaybackStreamsRequest): Promise<PlaybackLink[]> {
    const links = await this.playbackRepository.getPlaybackLinks(request.video);

    if (!request.refreshExpired) {
      return links;
    }

    const refreshedLinks: PlaybackLink[] = [];

    for (const link of links) {
      if (link.isExpired()) {
        try {
          const refreshed = await this.playbackRepository.refreshPlaybackLink(link);
          refreshedLinks.push(refreshed);
        } catch (error) {
          refreshedLinks.push(link);
        }
      } else {
        refreshedLinks.push(link);
      }
    }

    return refreshedLinks;
  }
}
