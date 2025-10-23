import { SourceConfig } from '../../entities/SourceConfig';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceId } from '../../value-objects/SourceId';
import { URL } from '../../value-objects/URL';

export interface EditSourceRequest {
  id: string;
  name?: string;
  apiUrl?: string;
  apiKey?: string;
  priority?: number;
  metadata?: Record<string, unknown>;
}

export class EditSourceUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request: EditSourceRequest): Promise<SourceConfig> {
    const sourceId = new SourceId(request.id);

    const source = await this.sourceConfigRepository.findById(sourceId);
    if (!source) {
      throw new Error(`Source not found: ${request.id}`);
    }

    if (request.name !== undefined) {
      source.setName(request.name);
    }

    if (request.apiUrl !== undefined) {
      source.setApiUrl(new URL(request.apiUrl));
    }

    if (request.apiKey !== undefined) {
      source.setApiKey(request.apiKey);
    }

    if (request.priority !== undefined) {
      source.setPriority(request.priority);
    }

    await this.sourceConfigRepository.update(source);

    return source;
  }
}
