/** Programmatic navigation without React Router (used in interceptors) */
export function navigateTo(path: string) {
  if (window.location.pathname !== path) {
    window.location.assign(path);
  }
}
