package com.autobook.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ChapterService {
    private final ChapterRepository chapterRepository;
    
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }
    
    public Chapter createChapter(Book book, String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Chapter title is required");
        }
        
        if (book == null) {
            throw new RuntimeException("Chapter must belong to a book");
        }
        
        Chapter chapter = new Chapter();
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setContent(content);
        
        return chapterRepository.save(chapter);
    }
    
    public Chapter getChapterById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
    }
    
    public List<Chapter> getChaptersByBook(Book book) {
        return chapterRepository.findByBook(book);
    }
    
    public Chapter updateChapter(Long chapterId, String title, String content) {
        Chapter chapter = getChapterById(chapterId);
        
        if (title != null && !title.trim().isEmpty()) {
            chapter.setTitle(title);
        }
        
        if (content != null) {
            chapter.setContent(content);
        }
        
        return chapterRepository.save(chapter);
    }
    
    public void deleteChapter(Long chapterId) {
        chapterRepository.deleteById(chapterId);
    }
    
    public Long getChapterCountByBook(Book book) {
        return chapterRepository.countByBook(book);
    }
    
    public List<Chapter> searchChaptersByContent(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }
        return chapterRepository.findByContentContainingIgnoreCase(searchText.trim());
    }
    
    public List<Chapter> searchChaptersInBook(Book book, String searchText) {
        List<Chapter> allChapters = chapterRepository.findByBook(book);
        
        if (searchText == null || searchText.trim().isEmpty()) {
            return allChapters;
        }
        
        String lowerSearch = searchText.trim().toLowerCase();
        
        return allChapters.stream()
                .filter(chapter -> 
                    chapter.getTitle().toLowerCase().contains(lowerSearch) ||
                    (chapter.getContent() != null && 
                     chapter.getContent().toLowerCase().contains(lowerSearch))
                )
                .toList();
    }
    
    public Chapter getNextChapter(Chapter currentChapter) {
        List<Chapter> allChapters = chapterRepository.findByBook(currentChapter.getBook());
        
        allChapters.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
        
        int currentIndex = -1;
        for (int i = 0; i < allChapters.size(); i++) {
            if (allChapters.get(i).getId().equals(currentChapter.getId())) {
                currentIndex = i;
                break;
            }
        }
        
        if (currentIndex >= 0 && currentIndex < allChapters.size() - 1) {
            return allChapters.get(currentIndex + 1);
        }
        
        return null;
    }
    
    public Chapter getPreviousChapter(Chapter currentChapter) {
        List<Chapter> allChapters = chapterRepository.findByBook(currentChapter.getBook());
        
        allChapters.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
        
        int currentIndex = -1;
        for (int i = 0; i < allChapters.size(); i++) {
            if (allChapters.get(i).getId().equals(currentChapter.getId())) {
                currentIndex = i;
                break;
            }
        }
        
        if (currentIndex > 0) {
            return allChapters.get(currentIndex - 1);
        }
        
        return null;
    }
    
    public Chapter getFirstChapter(Book book) {
        List<Chapter> chapters = chapterRepository.findByBook(book);
        
        if (chapters.isEmpty()) {
            return null;
        }
        
        Chapter firstChapter = chapters.get(0);
        for (Chapter chapter : chapters) {
            if (chapter.getCreatedAt().isBefore(firstChapter.getCreatedAt())) {
                firstChapter = chapter;
            }
        }
        
        return firstChapter;
    }
    
    public List<Chapter> importChapters(Book book, List<Chapter> chapters) {
        for (Chapter chapter : chapters) {
            chapter.setBook(book);
        }
        
        return chapterRepository.saveAll(chapters);
    }
}