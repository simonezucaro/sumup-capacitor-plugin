import { WebPlugin } from '@capacitor/core';

import type { SumupCapacitorPlugin } from './definitions';

export class SumupCapacitorWeb extends WebPlugin implements SumupCapacitorPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
