import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceId } from '../../value-objects/SourceId';

export interface DeleteSourceRequest {
  id: string;
}

export class DeleteSourceUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request: DeleteSourceRequest): Promise<void> {
    const sourceId = new SourceId(request.id);

    const exists = await this.sourceConfigRepository.exists(sourceId);
    if (!exists) {
      throw new Error(`Source not found: ${request.id}`);
    }

    await this.sourceConfigRepository.delete(sourceId);
  }
}
