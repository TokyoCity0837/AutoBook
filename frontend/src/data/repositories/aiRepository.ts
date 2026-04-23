import apiClient from '../api/apiClient';

export const aiRepository = {
    async analyzeStyle(bookId: number): Promise<any> {
        const r = await apiClient.post(`/ai/books/${bookId}/analyze-style`);
        return r.data;
    },

    async styleExists(bookId: number): Promise<any> {
        const r = await apiClient.get(`/ai/books/${bookId}/style-exists`);
        return r.data;
    },

    async getSuggestions(bookId: number, currentText: string, cursorContext: string): Promise<any> {
        const r = await apiClient.post(`/ai/books/${bookId}/suggestions`, {
            currentText,
            cursorContext,
        });
        return r.data;
    },

    async continueText(bookId: number, context: string, maxSentences = 3, temperature = 0.8): Promise<any> {
        const r = await apiClient.post(`/ai/books/${bookId}/continue`, {
            context,
            maxSentences,
            temperature,
        });
        return r.data;
    },
};