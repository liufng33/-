import { SourceConfig, SourceConfigProps } from '../../entities/SourceConfig';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceId } from '../../value-objects/SourceId';
import { SourceType, SourceTypeEnum } from '../../value-objects/SourceType';
import { URL } from '../../value-objects/URL';

export interface SourceDefinition {
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

export interface ImportInitialSourcesRequest {
  sources: SourceDefinition[];
  overwriteExisting?: boolean;
}

export interface ImportResult {
  imported: number;
  skipped: number;
  failed: number;
  errors: Array<{ sourceId: string; error: string }>;
}

export class ImportInitialSourcesUseCase {
  constructor(private readonly sourceConfigRepository: ISourceConfigRepository) {}

  async execute(request: ImportInitialSourcesRequest): Promise<ImportResult> {
    const result: ImportResult = {
      imported: 0,
      skipped: 0,
      failed: 0,
      errors: []
    };

    for (const sourceDef of request.sources) {
      try {
        const sourceId = new SourceId(sourceDef.id);
        const exists = await this.sourceConfigRepository.exists(sourceId);

        if (exists && !request.overwriteExisting) {
          result.skipped++;
          continue;
        }

        const props: SourceConfigProps = {
          id: sourceId,
          name: sourceDef.name,
          type: new SourceType(sourceDef.type),
          apiUrl: new URL(sourceDef.apiUrl),
          apiKey: sourceDef.apiKey,
          enabled: sourceDef.enabled ?? true,
          priority: sourceDef.priority ?? 0,
          rateLimit: sourceDef.rateLimit,
          metadata: sourceDef.metadata
        };

        const source = new SourceConfig(props);

        if (exists) {
          await this.sourceConfigRepository.update(source);
        } else {
          await this.sourceConfigRepository.save(source);
        }

        result.imported++;
      } catch (error) {
        result.failed++;
        result.errors.push({
          sourceId: sourceDef.id,
          error: error instanceof Error ? error.message : 'Unknown error'
        });
      }
    }

    return result;
  }
}
