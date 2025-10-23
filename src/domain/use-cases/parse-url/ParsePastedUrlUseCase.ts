import { VideoItem } from '../../entities/VideoItem';
import { IParserSourceRepository } from '../../repositories/IParserSourceRepository';

export interface ParsePastedUrlRequest {
  url: string;
}

export class ParsePastedUrlUseCase {
  constructor(private readonly parserSourceRepository: IParserSourceRepository) {}

  async execute(request: ParsePastedUrlRequest): Promise<VideoItem | null> {
    if (!request.url || request.url.trim().length === 0) {
      throw new Error('URL cannot be empty');
    }

    const url = request.url.trim();

    try {
      new globalThis.URL(url);
    } catch {
      throw new Error(`Invalid URL: ${url}`);
    }

    const parser = await this.parserSourceRepository.findParserForUrl(url);

    if (!parser) {
      return null;
    }

    if (!parser.isEnabled()) {
      throw new Error(`Parser is disabled: ${parser.getName()}`);
    }

    return await this.parserSourceRepository.parseVideoPage(parser, url);
  }
}
