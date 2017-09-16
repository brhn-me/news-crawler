package com.cn.crawler;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.cn.crawler.core.Config;
import com.cn.crawler.core.Crawler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import javax.inject.Inject;

@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties({Config.class})
@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner {
    @Inject
    private Crawler crawler;
    private final static Params params = new Params();

	public static void main(String[] args) {
	    try {
            JCommander.newBuilder()
                    .addObject(params)
                    .build()
                    .parse(args);
            SpringApplication.run(CrawlerApplication.class, args);
        } catch (ParameterException ex){
            System.err.println("Invalid parameters provided. " + ex.getMessage());
            ex.usage();
        }
	}

	@Override
	public void run(String... args) throws Exception {
        crawler.boostrap(params);
	}
}
