import { SourceConfig, SourceConfigProps } from '../../entities/SourceConfig';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceId } from '../../value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../../value-objects/SourceType';
import { URL } from '../../value-objects/URL';

export interface AddSourceRequest {
  id: string;
  name: string;
  type: SourceTypeEnum;
  apiUrl: string;
  apiKey?: string;
  enabled?: boolean;
  priority?: number;
  rateLimit?: number;
  metadata?: Record<string, unknown>;
}

export class AddSourceUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request: AddSourceRequest): Promise<SourceConfig> {
    const sourceId = new SourceId(request.id);

    const exists = await this.sourceConfigRepository.exists(sourceId);
    if (exists) {
      throw new Error(`Source with id '${request.id}' already exists`);
    }

    const props: SourceConfigProps = {
      id: sourceId,
      name: request.name,
      type: new SourceType(request.type),
      apiUrl: new URL(request.apiUrl),
      apiKey: request.apiKey,
      enabled: request.enabled,
      priority: request.priority,
      rateLimit: request.rateLimit,
      metadata: request.metadata
    };

    const source = new SourceConfig(props);
    await this.sourceConfigRepository.save(source);

    return source;
  }
}
