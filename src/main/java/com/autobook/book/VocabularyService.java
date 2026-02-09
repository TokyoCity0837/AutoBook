package com.autobook.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VocabularyService {
    private final VocabularyRepository vocabularyRepository;
    
    public VocabularyService(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }
    
    public Vocabulary addWordToVocabulary(Book book, String word, String meaning, String usageExample) {
        Optional<Vocabulary> existingWord = vocabularyRepository.findByBookAndWordIgnoreCase(book, word);
        if (existingWord.isPresent()) {
            throw new RuntimeException("Word '" + word + "' already exists in this book's vocabulary");
        }
        
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setBook(book);
        vocabulary.setWord(word);
        vocabulary.setMeaning(meaning);
        vocabulary.setUsageExample(usageExample);
        
        return vocabularyRepository.save(vocabulary);
    }
    
    public Vocabulary getWordById(Long wordId) {
        return vocabularyRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Vocabulary word not found"));
    }
    
    public List<Vocabulary> getVocabularyForBook(Book book) {
        return vocabularyRepository.findByBookOrderByWordAsc(book);
    }
    
    public List<Vocabulary> searchWordsInBook(Book book, String searchText) {
        List<Vocabulary> allWords = vocabularyRepository.findByBook(book);
        
        return allWords.stream()
                .filter(word -> word.getWord().toLowerCase().contains(searchText.toLowerCase()) ||
                                word.getMeaning().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
    }
    
    public List<Vocabulary> searchAllWords(String searchText) {
        return vocabularyRepository.findByWordContainingIgnoreCase(searchText);
    }
    
    public Vocabulary updateWord(Long wordId, String newMeaning, String newUsageExample) {
        Vocabulary word = getWordById(wordId);
        
        if (newMeaning != null && !newMeaning.trim().isEmpty()) {
            word.setMeaning(newMeaning);
        }
        
        if (newUsageExample != null && !newUsageExample.trim().isEmpty()) {
            word.setUsageExample(newUsageExample);
        }
        
        return vocabularyRepository.save(word);
    }
    
    public void deleteWord(Long wordId) {
        vocabularyRepository.deleteById(wordId);
    }
    
    public boolean wordExistsInBook(Book book, String word) {
        return vocabularyRepository.findByBookAndWordIgnoreCase(book, word).isPresent();
    }
    
    public Long getWordCountForBook(Book book) {
        return vocabularyRepository.countByBook(book);
    }
    
    public List<Vocabulary> addWordsFromText(Book book, List<Vocabulary> words) {
        for (Vocabulary word : words) {
            word.setBook(book);
            
            Optional<Vocabulary> existingWord = vocabularyRepository.findByBookAndWordIgnoreCase(book, word.getWord());
            if (existingWord.isPresent()) {
                throw new RuntimeException("Duplicate word found in batch: '" + word.getWord() + "'");
            }
        }
        
        return vocabularyRepository.saveAll(words);
    }
    
}