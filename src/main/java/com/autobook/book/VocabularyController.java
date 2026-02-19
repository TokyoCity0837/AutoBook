package com.autobook.book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/vocabulary")
public class VocabularyController {
    private final VocabularyService vocabularyService;
    private final BookService bookService;
    
    public VocabularyController(VocabularyService vocabularyService, BookService bookService) {
        this.vocabularyService = vocabularyService;
        this.bookService = bookService;
    }
    
    @PostMapping
    public ResponseEntity<Vocabulary> addWord(
            @PathVariable Long bookId,
            @RequestBody VocabularyRequest request) {
        Book book = bookService.getBookById(bookId);
        Vocabulary word = vocabularyService.addWordToVocabulary(
            book,
            request.getWord(),
            request.getMeaning(),
            request.getUsageExample()
        );
        return ResponseEntity.ok(word);
    }
    
    @GetMapping
    public ResponseEntity<List<Vocabulary>> getVocabulary(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        List<Vocabulary> words = vocabularyService.getVocabularyForBook(book);
        return ResponseEntity.ok(words);
    }
    
    @GetMapping("/{wordId}")
    public ResponseEntity<Vocabulary> getWordById(@PathVariable Long wordId) {
        Vocabulary word = vocabularyService.getWordById(wordId);
        return ResponseEntity.ok(word);
    }
    
    @PutMapping("/{wordId}")
    public ResponseEntity<Vocabulary> updateWord(
            @PathVariable Long wordId,
            @RequestBody VocabularyRequest request) {
        Vocabulary word = vocabularyService.updateWord(
            wordId,
            request.getMeaning(),
            request.getUsageExample()
        );
        return ResponseEntity.ok(word);
    }
    
    @DeleteMapping("/{wordId}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long wordId) {
        vocabularyService.deleteWord(wordId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Vocabulary>> searchWords(
            @PathVariable Long bookId,
            @RequestParam String query) {
        Book book = bookService.getBookById(bookId);
        List<Vocabulary> words = vocabularyService.searchWordsInBook(book, query);
        return ResponseEntity.ok(words);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getWordCount(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        Long count = vocabularyService.getWordCountForBook(book);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/random")
    public ResponseEntity<List<Vocabulary>> getRandomWords(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "5") int count){
        Book book = bookService.getBookById(bookId);
        List<Vocabulary> words = vocabularyService.getVocabularyForBook(book);
        return ResponseEntity.ok(words);
    }
    
    @GetMapping("/exists")
    public ResponseEntity<Boolean> wordExists(
            @PathVariable Long bookId,
            @RequestParam String word) {
        Book book = bookService.getBookById(bookId);
        Boolean exists = vocabularyService.wordExistsInBook(book, word);
        return ResponseEntity.ok(exists);
    }
}

class VocabularyRequest {
    private String word;
    private String meaning;
    private String usageExample;
    
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public String getUsageExample() { return usageExample; }
    public void setUsageExample(String usageExample) { this.usageExample = usageExample; }
}