import { URL } from '../value-objects/URL';

export interface VideoItemProps {
  id: string;
  title: string;
  url: URL;
  thumbnailUrl?: URL;
  duration?: number;
  description?: string;
  sourceId: string;
  publishDate?: Date;
  metadata?: Record<string, unknown>;
}

export class VideoItem {
  private readonly id: string;
  private readonly title: string;
  private readonly url: URL;
  private readonly thumbnailUrl?: URL;
  private readonly duration?: number;
  private readonly description?: string;
  private readonly sourceId: string;
  private readonly publishDate?: Date;
  private readonly metadata?: Record<string, unknown>;

  constructor(props: VideoItemProps) {
    if (!props.id || props.id.trim().length === 0) {
      throw new Error('VideoItem id cannot be empty');
    }
    if (!props.title || props.title.trim().length === 0) {
      throw new Error('VideoItem title cannot be empty');
    }
    if (!props.sourceId || props.sourceId.trim().length === 0) {
      throw new Error('VideoItem sourceId cannot be empty');
    }
    if (props.duration !== undefined && props.duration < 0) {
      throw new Error('VideoItem duration cannot be negative');
    }

    this.id = props.id.trim();
    this.title = props.title.trim();
    this.url = props.url;
    this.thumbnailUrl = props.thumbnailUrl;
    this.duration = props.duration;
    this.description = props.description?.trim();
    this.sourceId = props.sourceId.trim();
    this.publishDate = props.publishDate;
    this.metadata = props.metadata;
  }

  getId(): string {
    return this.id;
  }

  getTitle(): string {
    return this.title;
  }

  getUrl(): URL {
    return this.url;
  }

  getThumbnailUrl(): URL | undefined {
    return this.thumbnailUrl;
  }

  getDuration(): number | undefined {
    return this.duration;
  }

  getDescription(): string | undefined {
    return this.description;
  }

  getSourceId(): string {
    return this.sourceId;
  }

  getPublishDate(): Date | undefined {
    return this.publishDate;
  }

  getMetadata(): Record<string, unknown> | undefined {
    return this.metadata;
  }

  equals(other: VideoItem): boolean {
    return this.id === other.id && this.sourceId === other.sourceId;
  }
}
