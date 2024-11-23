package com.example.usctreehole;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

public class PostAdapterTestJUnit {

    private FirebaseFirestore mockFirestore;
    private CollectionReference mockCollectionReference;
    private DocumentReference mockDocumentReference;

    @Before
    public void setUp() {
        mockFirestore = mock(FirebaseFirestore.class);

        mockCollectionReference = mock(CollectionReference.class);
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);

        mockDocumentReference = mock(DocumentReference.class);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

    }

    @Test
    public void testPostAdapter() {
        FirebaseFirestore firestore = mockFirestore;

        firestore.collection("posts").document("postId");

        verify(mockFirestore).collection("posts");
        verify(mockCollectionReference).document("postId");
    }
}
