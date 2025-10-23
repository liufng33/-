import { PlaybackLink } from '../entities/PlaybackLink';
import { VideoItem } from '../entities/VideoItem';

export interface IPlaybackRepository {
  getPlaybackLinks(video: VideoItem): Promise<PlaybackLink[]>;
  
  getPlaybackLink(videoId: string, linkId: string): Promise<PlaybackLink | null>;
  
  refreshPlaybackLink(link: PlaybackLink): Promise<PlaybackLink>;
}
