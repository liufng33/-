import { SourceConfig } from '../../entities/SourceConfig';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceId } from '../../value-objects/SourceId';

export interface ToggleSourceRequest {
  id: string;
  enabled: boolean;
}

export class ToggleSourceUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request: ToggleSourceRequest): Promise<SourceConfig> {
    const sourceId = new SourceId(request.id);

    const source = await this.sourceConfigRepository.findById(sourceId);
    if (!source) {
      throw new Error(`Source not found: ${request.id}`);
    }

    if (request.enabled) {
      source.enable();
    } else {
      source.disable();
    }

    await this.sourceConfigRepository.update(source);

    return source;
  }
}
