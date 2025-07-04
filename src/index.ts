import { registerPlugin } from '@capacitor/core';

import type { SumupCapacitorPlugin } from './definitions';

const SumupCapacitor = registerPlugin<SumupCapacitorPlugin>('SumupCapacitor', {
  web: () => import('./web').then((m) => new m.SumupCapacitorWeb()),
});

export * from './definitions';
export { SumupCapacitor };
