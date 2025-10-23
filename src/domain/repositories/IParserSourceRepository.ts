import { ParserConfig } from '../entities/ParserConfig';
import { VideoItem } from '../entities/VideoItem';

export interface IParserSourceRepository {
  findParserForUrl(url: string): Promise<ParserConfig | null>;
  
  getAllParsers(): Promise<ParserConfig[]>;
  
  getActiveParsers(): Promise<ParserConfig[]>;
  
  parseVideoPage(parser: ParserConfig, url: string): Promise<VideoItem | null>;
}
