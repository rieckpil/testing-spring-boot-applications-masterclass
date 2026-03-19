import '@testing-library/jest-dom/vitest';
import {vi, afterEach} from 'vitest';
import {cleanup} from '@testing-library/react';

// @testing-library/react auto-cleanup requires Vitest globals; call it explicitly instead
afterEach(() => cleanup());

// jsdom does not implement ResizeObserver; Mantine's Select/ScrollArea requires it.
// Must be a class/function (not an arrow) so it can be called with `new`.
(global as unknown as Record<string, unknown>).ResizeObserver = class {
  observe = vi.fn();
  unobserve = vi.fn();
  disconnect = vi.fn();
};

// jsdom does not implement scrollIntoView; Mantine's Combobox calls it when scrolling options
Element.prototype.scrollIntoView = vi.fn();

// jsdom does not implement window.matchMedia; Mantine's color scheme hook requires it
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(function (query: string) {
    return {
      matches: false,
      media: query,
      onchange: null,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    };
  }),
});
