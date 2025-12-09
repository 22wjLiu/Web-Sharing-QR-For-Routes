const staticBaseUrl = import.meta.env.VITE_STATIC_BASE_URL || '';

export const buildAvatarUrl = (path?: string | null) => {
  if (!path) return '';
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  if (!staticBaseUrl) {
    return path;
  }
  const normalizedBase = staticBaseUrl.replace(/\/$/, '');
  const normalizedPath = path.startsWith('/') ? path.slice(1) : path;
  return `${normalizedBase}/${normalizedPath}`;
};
