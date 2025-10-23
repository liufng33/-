export enum SourceTypeEnum {
  SEARCH = 'SEARCH',
  PARSER = 'PARSER'
}

export class SourceType {
  private readonly value: SourceTypeEnum;

  constructor(value: SourceTypeEnum) {
    this.value = value;
  }

  getValue(): SourceTypeEnum {
    return this.value;
  }

  equals(other: SourceType): boolean {
    return this.value === other.value;
  }

  isSearch(): boolean {
    return this.value === SourceTypeEnum.SEARCH;
  }

  isParser(): boolean {
    return this.value === SourceTypeEnum.PARSER;
  }

  toString(): string {
    return this.value;
  }
}
