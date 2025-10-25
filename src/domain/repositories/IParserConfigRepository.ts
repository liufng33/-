import { ParserConfig } from '../entities/ParserConfig';

export interface IParserConfigRepository {
  findById(id: string): Promise<ParserConfig | null>;
  
  findAll(): Promise<ParserConfig[]>;
  
  findEnabled(): Promise<ParserConfig[]>;
  
  findByUrlPattern(url: string): Promise<ParserConfig[]>;
  
  save(parser: ParserConfig): Promise<void>;
  
  update(parser: ParserConfig): Promise<void>;
  
  delete(id: string): Promise<void>;
  
  exists(id: string): Promise<boolean>;
}
