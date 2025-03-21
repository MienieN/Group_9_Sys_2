package main.java.zenit.filesystem.metadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

class MetadataVerifierTest {
    private MetadataVerifier metadataVerifier;
    
    @BeforeEach
    void setUp() {
        metadataVerifier = spy(new MetadataVerifier());
    }
    
    @Test
    void verify ( ) {
        int result = metadataVerifier.verify(null);
        assertEquals(MetadataVerifier.METADATA_FILE_MISSING, result);
    }
}