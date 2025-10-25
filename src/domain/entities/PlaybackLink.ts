import { URL } from '../value-objects/URL';
import { Quality } from '../value-objects/Quality';

export enum PlaybackFormat {
  MP4 = 'MP4',
  HLS = 'HLS',
  DASH = 'DASH',
  WEBM = 'WEBM',
  OTHER = 'OTHER'
}

export interface PlaybackLinkProps {
  id: string;
  url: URL;
  quality: Quality;
  format: PlaybackFormat;
  videoId: string;
  headers?: Record<string, string>;
  expiresAt?: Date;
  requiresAuth?: boolean;
  metadata?: Record<string, unknown>;
}

export class PlaybackLink {
  private readonly id: string;
  private readonly url: URL;
  private readonly quality: Quality;
  private readonly format: PlaybackFormat;
  private readonly videoId: string;
  private readonly headers?: Record<string, string>;
  private readonly expiresAt?: Date;
  private readonly requiresAuth: boolean;
  private readonly metadata?: Record<string, unknown>;

  constructor(props: PlaybackLinkProps) {
    if (!props.id || props.id.trim().length === 0) {
      throw new Error('PlaybackLink id cannot be empty');
    }
    if (!props.videoId || props.videoId.trim().length === 0) {
      throw new Error('PlaybackLink videoId cannot be empty');
    }

    this.id = props.id.trim();
    this.url = props.url;
    this.quality = props.quality;
    this.format = props.format;
    this.videoId = props.videoId.trim();
    this.headers = props.headers;
    this.expiresAt = props.expiresAt;
    this.requiresAuth = props.requiresAuth ?? false;
    this.metadata = props.metadata;
  }

  getId(): string {
    return this.id;
  }

  getUrl(): URL {
    return this.url;
  }

  getQuality(): Quality {
    return this.quality;
  }

  getFormat(): PlaybackFormat {
    return this.format;
  }

  getVideoId(): string {
    return this.videoId;
  }

  getHeaders(): Record<string, string> | undefined {
    return this.headers ? { ...this.headers } : undefined;
  }

  getExpiresAt(): Date | undefined {
    return this.expiresAt;
  }

  requiresAuthentication(): boolean {
    return this.requiresAuth;
  }

  getMetadata(): Record<string, unknown> | undefined {
    return this.metadata;
  }

  isExpired(): boolean {
    if (!this.expiresAt) {
      return false;
    }
    return new Date() >= this.expiresAt;
  }

  equals(other: PlaybackLink): boolean {
    return this.id === other.id;
  }
}
