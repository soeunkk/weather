package com.soeunkk.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.soeunkk.weather.domain.Memo;
import com.soeunkk.weather.repository.JdbcMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional // 테스트 코드를 실행하고 db를 원상복구시킴, 정확히는 DB에 수정사항을 만들지 않는 테스트를 할 수 있게 함
public class JdbcMemoRepositoryTest {

	@Autowired
	JdbcMemoRepository jdbcMemoRepository;

	@Test
	void insertMemoTest() {
		// given
		Memo newMemo = new Memo(2, "insertMemoTest");

		// when
		jdbcMemoRepository.save(newMemo);

		// then
		Optional<Memo> result = jdbcMemoRepository.findById(2);
		assertEquals(result.get().getText(), "insertMemoTest");
	}

	@Test
	void findAllMemoTest() {
		List<Memo> memoList = jdbcMemoRepository.findAll();
		System.out.println(memoList);
		assertNotNull(memoList);
	}
}
