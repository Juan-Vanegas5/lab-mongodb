/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import com.example.models.Producto;
import com.example.models.ProductoDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {

        Competitor c = new Competitor();
        c.setAddress(competitor.getAddress());
        c.setAge(competitor.getAge());
        c.setCellphone(competitor.getCellphone());
        c.setCity(competitor.getCity());
        c.setCountry(competitor.getCountry());
        c.setName(competitor.getName());
        c.setSurname(competitor.getSurname());
        c.setTelephone(competitor.getTelephone());
        c.setVehicle(competitor.getVehicle());

        return guardarConProducto(c, competitor.getProducto());
    }

    @POST
    @Path("/vehicle")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitorWithVehicle(CompetitorDTO competitor) {

        Competitor c = new Competitor();
        c.setAddress(competitor.getAddress());
        c.setAge(competitor.getAge());
        c.setCellphone(competitor.getCellphone());
        c.setCity(competitor.getCity());
        c.setCountry(competitor.getCountry());
        c.setName(competitor.getName());
        c.setSurname(competitor.getSurname());
        c.setTelephone(competitor.getTelephone());
        c.setVehicle(competitor.getVehicle());

        return guardarConProducto(c, competitor.getProducto());
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompetitorsByName(@PathParam("name") String name) {

        TypedQuery<Competitor> query = (TypedQuery<Competitor>) entityManager.createQuery("SELECT c FROM Competitor c WHERE c.name = :name");
        List<Competitor> competitors = query.setParameter("name", name).getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    /**
     * Actividad 4: nombres de los competidores que empiezan por la letra 'A'.
     * EclipseLink traduce el LIKE a una expresion regular de MongoDB (^A.*).
     */
    @GET
    @Path("/names/startsWithA")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNamesStartingWithA() {

        Query q = entityManager.createQuery("SELECT c FROM Competitor c WHERE c.name LIKE 'A%' ORDER BY c.name ASC");
        List<Competitor> competitors = q.getResultList();
        JSONArray names = new JSONArray();
        for (Competitor c : competitors) {
            names.add(c.getName());
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(names.toJSONString()).build();
    }

    /**
     * Actividad 2: todo competidor DEBE tener 1 producto asociado.
     * Si el producto no viene completo se responde 400 y no se guarda nada.
     */
    private Response guardarConProducto(Competitor c, ProductoDTO productoDTO) {

        JSONObject rta = new JSONObject();
        Producto producto = crearProducto(productoDTO);
        if (producto == null) {
            entityManager.close();
            rta.put("error", "El producto es obligatorio: debe enviar nombre, marca y fechaCompra (yyyy-MM-dd)");
            return Response.status(400).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(producto);
            c.setProducto(producto);
            entityManager.persist(c);
            entityManager.getTransaction().commit();
            entityManager.refresh(c);
            rta.put("competitor_id", c.getId());
            rta.put("producto_id", producto.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            c = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    private Producto crearProducto(ProductoDTO dto) {
        if (dto == null || vacio(dto.getNombre()) || vacio(dto.getMarca()) || vacio(dto.getFechaCompra())) {
            return null;
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        Calendar fecha = Calendar.getInstance();
        try {
            fecha.setTime(formato.parse(dto.getFechaCompra()));
        } catch (ParseException e) {
            return null;
        }
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setMarca(dto.getMarca());
        p.setFechaCompra(fecha);
        return p;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }

}
