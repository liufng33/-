import { SourceConfig } from '../entities/SourceConfig';
import { SourceId } from '../value-objects/SourceId';
import { SourceTypeEnum } from '../value-objects/SourceType';

export interface ISourceConfigRepository {
  findById(id: SourceId): Promise<SourceConfig | null>;
  
  findAll(): Promise<SourceConfig[]>;
  
  findByType(type: SourceTypeEnum): Promise<SourceConfig[]>;
  
  findEnabled(): Promise<SourceConfig[]>;
  
  save(source: SourceConfig): Promise<void>;
  
  update(source: SourceConfig): Promise<void>;
  
  delete(id: SourceId): Promise<void>;
  
  exists(id: SourceId): Promise<boolean>;
}
