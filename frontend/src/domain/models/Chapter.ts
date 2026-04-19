// ─── Chapter ────────────────────────────────────────────

export interface Chapter {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface ChapterCreateRequest {
  title: string;
  content: string;
}

export interface ChapterUpdateRequest {
  title?: string;
  content?: string;
}
