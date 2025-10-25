export class URL {
  private readonly value: string;

  constructor(value: string) {
    if (!this.isValidUrl(value)) {
      throw new Error(`Invalid URL: ${value}`);
    }
    this.value = value;
  }

  private isValidUrl(url: string): boolean {
    try {
      new globalThis.URL(url);
      return true;
    } catch {
      return false;
    }
  }

  getValue(): string {
    return this.value;
  }

  equals(other: URL): boolean {
    return this.value === other.value;
  }

  toString(): string {
    return this.value;
  }
}
