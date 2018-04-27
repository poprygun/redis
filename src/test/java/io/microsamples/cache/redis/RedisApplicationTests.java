package io.microsamples.cache.redis;

import io.microsamples.cache.redis.config.EmbededRedisTestConfiguration;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbededRedisTestConfiguration.class)
public class RedisApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CacheManager cacheManager;

	@After
	public void clearCache(){
		cacheManager.getCache("days").clear();
		cacheManager.getCache("months").clear();
	}


	@Test
	public void shouldSaveDaysToCache() throws Exception {
		cacheData("days");

		verifyDataInCache("/cached?cacheName=days", "DaysService");
	}

	@Test
	public void shouldSaveMonthsToCache()throws Exception{
		cacheData("months");

		verifyDataInCache("/cached?cacheName=months", "MonthsService");
	}


	@Test
	public void shouldDeleteDaysFromCache() throws Exception {
		verifyCacheDelete("days", "months", "MonthsService");
	}

	@Test
	public void shouldDeleteMonthsFromCache() throws Exception {
		verifyCacheDelete("months", "days", "DaysService");
	}

	private void verifyCacheDelete(String cacheNameToDelete, String cacheNameToRetain, String verificationForRetainedCache) throws Exception {
		shouldSaveDaysToCache();
		shouldSaveMonthsToCache();

		deleteCacheFor(cacheNameToDelete);

		verifyDataInCache("/cached?cacheName=".concat(cacheNameToRetain), verificationForRetainedCache);

		String dataThatShoudBeGone = getDataFromCache("/cached?cacheName=".concat(cacheNameToDelete));
		assertThat(dataThatShoudBeGone, IsEqual.equalTo("{}"));
	}



	private void cacheData(String cacheName) throws Exception {
		performGet("/", cacheName);
	}

	private void deleteCacheFor(String cacheName) throws Exception {
		performGet("/purge?cacheName=", cacheName);
	}

	private void performGet(String url, String cacheName) throws Exception{
		this.mockMvc.perform(get(url.concat(cacheName)))
//				.andDo(print())
				.andExpect(status().isOk());
	}

	private void verifyDataInCache(String url, String toVerify) throws Exception {
		String content = getDataFromCache(url);
		assertThat(content, containsString(toVerify));
	}

	private String getDataFromCache(String url) throws Exception {
		MvcResult result = this.mockMvc.perform(get(url))
//				.andDo(print())
				.andExpect(status().isOk()).andReturn();

		return result.getResponse().getContentAsString();
	}

}
