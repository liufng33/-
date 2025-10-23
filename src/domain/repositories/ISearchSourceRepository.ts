import { VideoItem } from '../entities/VideoItem';
import { SourceConfig } from '../entities/SourceConfig';

export interface SearchOptions {
  query: string;
  limit?: number;
  offset?: number;
  filters?: Record<string, unknown>;
}

export interface SearchResult {
  items: VideoItem[];
  total: number;
  hasMore: boolean;
}

export interface ISearchSourceRepository {
  search(source: SourceConfig, options: SearchOptions): Promise<SearchResult>;
  
  getActiveSearchSources(): Promise<SourceConfig[]>;
  
  healthCheck(source: SourceConfig): Promise<boolean>;
}
