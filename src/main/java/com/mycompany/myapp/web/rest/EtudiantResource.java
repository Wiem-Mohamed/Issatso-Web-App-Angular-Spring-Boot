package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Etudiant;
import com.mycompany.myapp.domain.enumeration.Filiere;
import com.mycompany.myapp.repository.EtudiantRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.NumInscriptionAlreadyUsedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Etudiant}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EtudiantResource {

    private final Logger log = LoggerFactory.getLogger(EtudiantResource.class);

    private static final String ENTITY_NAME = "etudiant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtudiantRepository etudiantRepository;

    public EtudiantResource(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    /**
     * {@code POST  /etudiants} : Create a new etudiant.
     *
     * @param etudiant the etudiant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etudiant, or with status {@code 400 (Bad Request)} if the etudiant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/etudiants")
    public ResponseEntity<Etudiant> createEtudiant(@RequestBody Etudiant etudiant) throws URISyntaxException {
        log.debug("REST request to save Etudiant : {}", etudiant);

        // Vérifier si un etudiant avec le même num inscription existe déjà
        Optional<Etudiant> existingEtudiant = etudiantRepository.findByNumInscription(etudiant.getNumInscription());
        if (existingEtudiant.isPresent()) {
            throw new NumInscriptionAlreadyUsedException();
        }

        if (etudiant.getId() != null) {
            throw new BadRequestAlertException("A new etudiant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Etudiant result = etudiantRepository.save(etudiant);
        return ResponseEntity
            .created(new URI("/api/etudiants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /etudiants/:id} : Updates an existing etudiant.
     *
     * @param id the id of the etudiant to save.
     * @param etudiant the etudiant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etudiant,
     * or with status {@code 400 (Bad Request)} if the etudiant is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etudiant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/etudiants/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Etudiant etudiant
    ) throws URISyntaxException {
        log.debug("REST request to update Etudiant : {}, {}", id, etudiant);
        if (etudiant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etudiant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etudiantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Etudiant result = etudiantRepository.save(etudiant);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etudiant.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /etudiants/:id} : Partial updates given fields of an existing etudiant, field will ignore if it is null
     *
     * @param id the id of the etudiant to save.
     * @param etudiant the etudiant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etudiant,
     * or with status {@code 400 (Bad Request)} if the etudiant is not valid,
     * or with status {@code 404 (Not Found)} if the etudiant is not found,
     * or with status {@code 500 (Internal Server Error)} if the etudiant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/etudiants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Etudiant> partialUpdateEtudiant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Etudiant etudiant
    ) throws URISyntaxException {
        log.debug("REST request to partial update Etudiant partially : {}, {}", id, etudiant);
        if (etudiant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etudiant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etudiantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Etudiant> result = etudiantRepository
            .findById(etudiant.getId())
            .map(existingEtudiant -> {
                if (etudiant.getNom() != null) {
                    existingEtudiant.setNom(etudiant.getNom());
                }
                if (etudiant.getPrenom() != null) {
                    existingEtudiant.setPrenom(etudiant.getPrenom());
                }
                if (etudiant.getEmail() != null) {
                    existingEtudiant.setEmail(etudiant.getEmail());
                }
                if (etudiant.getNumInscription() != null) {
                    existingEtudiant.setNumInscription(etudiant.getNumInscription());
                }
                if (etudiant.getDateAffectation() != null) {
                    existingEtudiant.setDateAffectation(etudiant.getDateAffectation());
                }
                if (etudiant.getFiliere() != null) {
                    existingEtudiant.setFiliere(etudiant.getFiliere());
                }
                if (etudiant.getNiveau() != null) {
                    existingEtudiant.setNiveau(etudiant.getNiveau());
                }

                return existingEtudiant;
            })
            .map(etudiantRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etudiant.getId().toString())
        );
    }

    /**
     * {@code GET  /etudiants} : get all the etudiants.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etudiants in body.
     */
    @GetMapping("/etudiants")
    public List<Etudiant> getAllEtudiants(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Etudiants");
        if (eagerload) {
            return etudiantRepository.findAllWithEagerRelationships();
        } else {
            return etudiantRepository.findAll();
        }
    }

    /**
     * {@code GET  /etudiants/:id} : get the "id" etudiant.
     *
     * @param id the id of the etudiant to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etudiant, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/etudiants/{id}")
    public ResponseEntity<Etudiant> getEtudiant(@PathVariable Long id) {
        log.debug("REST request to get Etudiant : {}", id);
        Optional<Etudiant> etudiant = etudiantRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(etudiant);
    }

    /**
     * {@code DELETE  /etudiants/:id} : delete the "id" etudiant.
     *
     * @param id the id of the etudiant to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/etudiants/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        log.debug("REST request to delete Etudiant : {}", id);
        etudiantRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/mailetudiant/{email}")
    public ResponseEntity<Long> getEtudiantIdByEmail(@PathVariable String email) {
        log.debug("REST request to get Etudiant ID by email: {}", email);
        Long etudiantId = etudiantRepository.findIdByEmailEtudiant(email);
        if (etudiantId != null) {
            return ResponseEntity.ok().body(etudiantId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/idgroupe/{etudiantId}")
    public ResponseEntity<Long> getGroupeIdByEtudiantId(@PathVariable Long etudiantId) {
        Long groupeId = etudiantRepository.findGroupIdByEtudiantId(etudiantId);
        return ResponseEntity.ok(groupeId);
    }

    @GetMapping("/listeg/{etudiantId}")
    public ResponseEntity<List<Etudiant>> getEtudiantsSameGroupe(@PathVariable Long etudiantId) {
        List<Etudiant> etudiantsMemeGroupe = etudiantRepository.findEtudiantsBySameGroupe(etudiantId);
        return ResponseEntity.ok(etudiantsMemeGroupe);
    }
}
