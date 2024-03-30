import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EnseignantService } from '../service/enseignant.service';

import { EnseignantComponent } from './enseignant.component';

describe('Enseignant Management Component', () => {
  let comp: EnseignantComponent;
  let fixture: ComponentFixture<EnseignantComponent>;
  let service: EnseignantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'enseignant', component: EnseignantComponent }]),
        HttpClientTestingModule,
        EnseignantComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(EnseignantComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnseignantComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EnseignantService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.enseignants?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to enseignantService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getEnseignantIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getEnseignantIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});