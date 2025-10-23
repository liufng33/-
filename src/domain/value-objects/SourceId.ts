export class SourceId {
  private readonly value: string;

  constructor(value: string) {
    if (!value || value.trim().length === 0) {
      throw new Error('SourceId cannot be empty');
    }
    this.value = value.trim();
  }

  getValue(): string {
    return this.value;
  }

  equals(other: SourceId): boolean {
    return this.value === other.value;
  }

  toString(): string {
    return this.value;
  }
}
