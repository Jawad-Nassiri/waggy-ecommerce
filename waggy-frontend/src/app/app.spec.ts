import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideRouter } from '@angular/router';
import { AuthService } from './services/auth.service';

describe('App', () => {
  const authServiceMock = {
    loadCurrentUser: vi.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    })
      .overrideComponent(App, {
        set: {
          template: '',
          imports: [],
        },
      })
      .compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;

    expect(app).toBeTruthy();
  });
});