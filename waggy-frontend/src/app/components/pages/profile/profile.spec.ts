import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { Profile } from './profile';

describe('Profile', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;

  beforeEach(async () => {
    const authServiceMock = {
      currentUser: () => null,
      logout: () => {},
    };

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        {
          provide: AuthService,
          useValue: {
            currentUser: () => null,
            logout: () => {},
          },
        },
        {
          provide: Router,
          useValue: {
            navigate: () => {},
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
