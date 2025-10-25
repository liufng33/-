import { SourceConfig } from '../../entities/SourceConfig';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceTypeEnum } from '../../value-objects/SourceType';

export interface ListSourcesRequest {
  type?: SourceTypeEnum;
  enabledOnly?: boolean;
}

export class ListSourcesUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request?: ListSourcesRequest): Promise<SourceConfig[]> {
    if (!request) {
      return await this.sourceConfigRepository.findAll();
    }

    if (request.enabledOnly) {
      const sources = await this.sourceConfigRepository.findEnabled();
      if (request.type) {
        return sources.filter(s => s.getType().getValue() === request.type);
      }
      return sources;
    }

    if (request.type) {
      return await this.sourceConfigRepository.findByType(request.type);
    }

    return await this.sourceConfigRepository.findAll();
  }
}
