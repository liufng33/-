import { ImportInitialSourcesUseCase } from './ImportInitialSourcesUseCase';
import { ISourceConfigRepository } from '../../repositories/ISourceConfigRepository';
import { SourceTypeEnum } from '../../value-objects/SourceType';

describe('ImportInitialSourcesUseCase', () => {
  let useCase: ImportInitialSourcesUseCase;
  let mockRepository: jest.Mocked<ISourceConfigRepository>;

  beforeEach(() => {
    mockRepository = {
      findById: jest.fn(),
      findAll: jest.fn(),
      findByType: jest.fn(),
      findEnabled: jest.fn(),
      save: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      exists: jest.fn()
    };

    useCase = new ImportInitialSourcesUseCase(mockRepository);
  });

  describe('execute', () => {
    it('should import new sources successfully', async () => {
      mockRepository.exists.mockResolvedValue(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        sources: [
          {
            id: 'source-1',
            name: 'Source 1',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api1.example.com'
          },
          {
            id: 'source-2',
            name: 'Source 2',
            type: SourceTypeEnum.PARSER,
            apiUrl: 'https://api2.example.com'
          }
        ]
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(2);
      expect(result.skipped).toBe(0);
      expect(result.failed).toBe(0);
      expect(result.errors).toHaveLength(0);
      expect(mockRepository.save).toHaveBeenCalledTimes(2);
    });

    it('should skip existing sources when overwriteExisting is false', async () => {
      mockRepository.exists.mockResolvedValueOnce(true).mockResolvedValueOnce(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        sources: [
          {
            id: 'existing-source',
            name: 'Existing Source',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api1.example.com'
          },
          {
            id: 'new-source',
            name: 'New Source',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api2.example.com'
          }
        ],
        overwriteExisting: false
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(1);
      expect(result.skipped).toBe(1);
      expect(result.failed).toBe(0);
      expect(mockRepository.save).toHaveBeenCalledTimes(1);
    });

    it('should overwrite existing sources when overwriteExisting is true', async () => {
      mockRepository.exists.mockResolvedValue(true);
      mockRepository.update.mockResolvedValue();

      const request = {
        sources: [
          {
            id: 'existing-source',
            name: 'Updated Source',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api.example.com'
          }
        ],
        overwriteExisting: true
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(1);
      expect(result.skipped).toBe(0);
      expect(result.failed).toBe(0);
      expect(mockRepository.update).toHaveBeenCalledTimes(1);
      expect(mockRepository.save).not.toHaveBeenCalled();
    });

    it('should handle validation errors and continue with other sources', async () => {
      mockRepository.exists.mockResolvedValue(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        sources: [
          {
            id: 'valid-source',
            name: 'Valid Source',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api.example.com'
          },
          {
            id: 'invalid-source',
            name: '',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api.example.com'
          },
          {
            id: 'another-valid',
            name: 'Another Valid',
            type: SourceTypeEnum.PARSER,
            apiUrl: 'https://api2.example.com'
          }
        ]
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(2);
      expect(result.skipped).toBe(0);
      expect(result.failed).toBe(1);
      expect(result.errors).toHaveLength(1);
      expect(result.errors[0].sourceId).toBe('invalid-source');
      expect(result.errors[0].error).toContain('name cannot be empty');
    });

    it('should handle repository errors', async () => {
      mockRepository.exists.mockResolvedValueOnce(false).mockResolvedValueOnce(false);
      mockRepository.save
        .mockResolvedValueOnce()
        .mockRejectedValueOnce(new Error('Database error'));

      const request = {
        sources: [
          {
            id: 'source-1',
            name: 'Source 1',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api1.example.com'
          },
          {
            id: 'source-2',
            name: 'Source 2',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api2.example.com'
          }
        ]
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(1);
      expect(result.skipped).toBe(0);
      expect(result.failed).toBe(1);
      expect(result.errors).toHaveLength(1);
      expect(result.errors[0].error).toBe('Database error');
    });

    it('should import with all optional fields', async () => {
      mockRepository.exists.mockResolvedValue(false);
      mockRepository.save.mockResolvedValue();

      const request = {
        sources: [
          {
            id: 'source-1',
            name: 'Source 1',
            type: SourceTypeEnum.SEARCH,
            apiUrl: 'https://api.example.com',
            apiKey: 'secret-key',
            enabled: true,
            priority: 10,
            rateLimit: 100,
            metadata: { custom: 'value' }
          }
        ]
      };

      const result = await useCase.execute(request);

      expect(result.imported).toBe(1);
      expect(result.failed).toBe(0);
      expect(mockRepository.save).toHaveBeenCalled();
    });
  });
});
