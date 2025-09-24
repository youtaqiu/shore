package run.vexa.reactor.r2dbc.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseDomainTest {

    @Test
    void testDefaultConstructor() {
        // Given & When
        BaseDomain<Long> domain = new BaseDomain<>();
        
        // Then
        assertNull(domain.getId());
        assertNull(domain.getCreateBy());
        assertNull(domain.getCreateTime());
        assertNull(domain.getUpdateBy());
        assertNull(domain.getUpdateTime());
        assertFalse(domain.isDeleted());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        BaseDomain<Long> domain = new BaseDomain<>(
            1L,            // id
            "creator",     // createBy
            now.minusDays(1), // createTime
            "updater",     // updateBy
            now,           // updateTime
            false          // deleted
        );
        
        // Then
        assertEquals(1L, domain.getId());
        assertEquals("creator", domain.getCreateBy());
        assertEquals(now.minusDays(1), domain.getCreateTime());
        assertEquals("updater", domain.getUpdateBy());
        assertEquals(now, domain.getUpdateTime());
        assertFalse(domain.isDeleted());
    }

    @Test
    void testBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        BaseDomain<Long> domain = BaseDomain.<Long>builder()
            .id(2L)
            .createBy("test-builder")
            .createTime(now.minusHours(1))
            .updateBy("test-updater")
            .updateTime(now)
            .deleted(true)
            .build();
        
        // Then
        assertEquals(2L, domain.getId());
        assertEquals("test-builder", domain.getCreateBy());
        assertEquals(now.minusHours(1), domain.getCreateTime());
        assertEquals("test-updater", domain.getUpdateBy());
        assertEquals(now, domain.getUpdateTime());
        assertTrue(domain.isDeleted());
    }

    @Test
    void testSetters() {
        // Given
        BaseDomain<Long> domain = new BaseDomain<>();
        LocalDateTime now = LocalDateTime.now();
        
        // When
        domain.setId(3L);
        domain.setCreateBy("setter-test");
        domain.setCreateTime(now.minusDays(2));
        domain.setUpdateBy("setter-updater");
        domain.setUpdateTime(now.minusDays(1));
        domain.setDeleted(true);
        
        // Then
        assertEquals(3L, domain.getId());
        assertEquals("setter-test", domain.getCreateBy());
        assertEquals(now.minusDays(2), domain.getCreateTime());
        assertEquals("setter-updater", domain.getUpdateBy());
        assertEquals(now.minusDays(1), domain.getUpdateTime());
        assertTrue(domain.isDeleted());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        BaseDomain<Long> domain1 = BaseDomain.<Long>builder()
            .id(1L)
            .createBy("test")
            .createTime(now)
            .updateBy("test")
            .updateTime(now)
            .deleted(false)
            .build();
            
        // Create an identical domain (all fields the same)
        BaseDomain<Long> domain1Copy = BaseDomain.<Long>builder()
            .id(1L)
            .createBy("test")
            .createTime(now)
            .updateBy("test")
            .updateTime(now)
            .deleted(false)
            .build();
            
        // Create a domain with different ID
        BaseDomain<Long> domainDifferentId = BaseDomain.<Long>builder()
            .id(2L)
            .createBy("test")
            .createTime(now)
            .updateBy("test")
            .updateTime(now)
            .deleted(false)
            .build();
            
        // Create a domain with different createBy
        BaseDomain<Long> domainDifferentCreateBy = BaseDomain.<Long>builder()
            .id(1L)
            .createBy("different")
            .createTime(now)
            .updateBy("test")
            .updateTime(now)
            .deleted(false)
            .build();
            
        // Create a domain with null ID
        BaseDomain<Long> domainNullId = new BaseDomain<>();
        domainNullId.setId(null);
        
        // Create another domain with null ID
        BaseDomain<Long> domainAlsoNullId = new BaseDomain<>();
        domainAlsoNullId.setId(null);
        
        // Then
        // Test equals() contract
        
        // Symmetry
        assertEquals(domain1, domain1Copy, "Objects with same field values should be equal");
        assertEquals(domain1Copy, domain1, "Equals should be symmetric");
        
        // Different ID
        assertNotEquals(domain1, domainDifferentId, "Objects with different IDs should not be equal");
        
        // Different field
        assertNotEquals(domain1, domainDifferentCreateBy, "Objects with different field values should not be equal");
        
        // Null ID cases
        assertNotEquals(domain1, domainNullId, "Domain with non-null ID should not equal domain with null ID");
        assertNotEquals(domainNullId, domain1, "Domain with null ID should not equal domain with non-null ID");
        
        // Two domains with null IDs should be equal if all other fields are equal
        assertEquals(domainNullId, domainAlsoNullId, "Two domains with null IDs and same field values should be equal");
        
        // Test with null
        assertNotEquals(null, domain1, "Object should not be equal to null");
        
        // Test with different class
        assertNotEquals("not a BaseDomain", domain1, "Should not be equal to object of different type");
        
        // Test hash code contract
        
        // Consistency
        int initialHashCode = domain1.hashCode();
        assertEquals(initialHashCode, domain1.hashCode(), "Hash code should be consistent");
        
        // Equal objects have equal hash codes
        assertEquals(domain1.hashCode(), domain1Copy.hashCode(), "Equal objects must have equal hash codes");
        
        // Different objects should (usually) have different hash codes
        // Note: This is not strictly required by the contract but is generally desirable
        assertNotEquals(domain1.hashCode(), domainDifferentId.hashCode(), "Different objects should have different hash codes");
    }

    @Test
    void testToString() {
        // Given
        BaseDomain<Long> domain = BaseDomain.<Long>builder()
            .id(1L)
            .createBy("tester")
            .createTime(LocalDateTime.of(2023, 1, 1, 0, 0))
            .updateBy("updater")
            .updateTime(LocalDateTime.of(2023, 1, 2, 0, 0))
            .deleted(false)
            .build();
        
        // When
        String toString = domain.toString();
        
        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("createBy=tester"));
        assertTrue(toString.contains("updateBy=updater"));
        assertTrue(toString.contains("deleted=false"));
    }
}
