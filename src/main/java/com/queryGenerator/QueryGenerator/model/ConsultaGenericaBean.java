package com.queryGenerator.QueryGenerator.model;

import com.queryGenerator.QueryGenerator.service.ConsultaGenericaService;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

@Named
public class ConsultaGenericaBean {

    @Autowired
    ConsultaGenericaService consultaGenericaService;

    List<Object> dataSource;
    List<Object> listaResultadosConsulta;
    FacesMessage message;

    private LazyDataModel<Object> lazyModel;

//    String consultaHQL = "select new com.queryGenerator.QueryGenerator.entity.VistaMovimientoContableActivoFijo (\n"
//            + "                vmc.fechaAplicacion, \n"
//            + "                vmc.numeroAsiento)\n"
//            + "                from  VistaMovimientoContableActivoFijo   vmc";

    String consultaHQL = "select cat.id, (select max (kit.weight)"
            + " from cat.kitten kit where kit.weight = 100)"
            + " from Cat as cat"
            + " where cat.name = some (select name.nickName from Name as name)";


    int tipoConsulta = 1;

    Map<String, Object> listaParametros = new HashMap<String, Object>();

    @PostConstruct
    public void init() {
        dataSource = new ArrayList<Object>();
        listaResultadosConsulta = new ArrayList<Object>();

        consultaGenericaService.fragmentaConsultaHql(new StringBuilder(consultaHQL));

        if (tipoConsulta == 1) {
            listaResultadosConsulta = consultaGenericaService.getResultadosConsulta(consultaHQL);
        }
    }

    @PostConstruct
    public void lazyLoad() {
        lazyModel = new LazyDataModel<Object>() {
            @Override
            public List<Object> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

                long rowCount = consultaGenericaService.countResultadosConsulta(filterBy, consultaHQL, listaParametros);

                List<Object> resultadosConsulta = consultaGenericaService.getResultadosConsulta(offset, pageSize, sortBy, filterBy, consultaHQL, listaParametros);

//              long rowCount = resultadosConsulta.size() < pageSize ? offset + resultadosConsulta.size() : offset + pageSize + 1;
                setRowCount((int) rowCount);// Setea el número total de filas de la consulta para establecer la ultima pagina

                return resultadosConsulta;

            }
        };
    }

    public int getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(int tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getConsultaHQL() {
        return consultaHQL;
    }

    public void setConsultaHQL(String consultaHQL) {
        this.consultaHQL = consultaHQL;
    }


    public List<Object> getListaResultadosConsulta() {
        return listaResultadosConsulta;
    }

    public void setListaResultadosConsulta(List<Object> listaResultadosConsulta) {
        this.listaResultadosConsulta = listaResultadosConsulta;
    }

    public LazyDataModel<Object> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Object> lazyModel) {
        this.lazyModel = lazyModel;
    }

}
