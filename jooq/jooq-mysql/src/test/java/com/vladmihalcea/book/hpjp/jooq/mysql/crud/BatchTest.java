package com.vladmihalcea.book.hpjp.jooq.mysql.crud;

import org.jooq.BatchBindStep;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vladmihalcea.book.hpjp.jooq.mysql.schema.Tables.POST;
import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Mihalcea
 */
public class BatchTest extends AbstractJOOQMySQLIntegrationTest {

    @Override
    protected String ddlScript() {
        return "initial_schema.sql";
    }

    @Test
    public void testBatching() {
        doInJOOQ(context -> {
            context.delete(POST).execute();
            BatchBindStep batch = context.batch(context
                .insertInto(POST, POST.TITLE)
                .values("?")
            );
            for (int i = 0; i < 3; i++) {
                batch.bind(String.format("Post no. %d", i));
            }
            int[] insertCounts = batch.execute();
            assertEquals(3, insertCounts.length);
            Result<Record> posts = context.select().from(POST).fetch();
            assertEquals(3, posts.size());
        });
    }

    @Test @Ignore("values(Collection) is not INSERT INTO ... VALUES ( (..) (..) (..) )")
    public void testBatchingWithCollection() {
        doInJOOQ(context -> {
            context.delete(POST).execute();

            int insertCount = context
            .insertInto(POST, POST.TITLE)
            .values(IntStream.range(1, 3).boxed()
                    .map(i -> String.format("Post no. %d", i))
                    .collect(Collectors.toList()))
            .execute();
            assertEquals(3, insertCount);
            Result<Record> posts = context.select().from(POST).fetch();
            assertEquals(3, posts.size());
        });
    }
}
