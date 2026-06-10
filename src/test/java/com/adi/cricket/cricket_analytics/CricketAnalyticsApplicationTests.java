package com.adi.cricket.cricket_analytics;

import com.adi.cricket.cricket_analytics.parser.StartupRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CricketAnalyticsApplicationTests {

	private final ApplicationContextRunner contextRunner =
			new ApplicationContextRunner()
					.withUserConfiguration(CricketAnalyticsApplication.class)
					.withPropertyValues(
							"spring.datasource.url=jdbc:h2:mem:cricket-analytics-test;DB_CLOSE_DELAY=-1",
							"spring.datasource.driver-class-name=org.h2.Driver",
							"spring.datasource.username=sa",
							"spring.datasource.password=",
							"spring.jpa.hibernate.ddl-auto=create-drop",
							"spring.jpa.show-sql=false"
					);

	@Test
	void contextLoadsWithoutStartupImportEnabled() {
		contextRunner.run(context ->
				assertThat(context)
						.hasNotFailed()
						.doesNotHaveBean(StartupRunner.class));
	}

	@Test
	void registersStartupRunnerWhenImportIsExplicitlyEnabled() {
		contextRunner
				.withPropertyValues("cricket.import-on-startup=true")
				.run(context ->
						assertThat(context)
								.hasNotFailed()
								.hasSingleBean(StartupRunner.class));
	}

}
