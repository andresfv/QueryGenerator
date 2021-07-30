package com.queryGenerator.QueryGenerator;

import com.queryGenerator.QueryGenerator.entity.EstadoActivoFijo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class QueryGeneratorApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(QueryGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        String sql = " select * from estado_activo_fijo";
//
//        List<EstadoActivoFijo> listaEstados = jdbcTemplate.query(sql,
//                BeanPropertyRowMapper.newInstance(EstadoActivoFijo.class));
//
//        listaEstados.forEach(System.out::println);
    }

}
