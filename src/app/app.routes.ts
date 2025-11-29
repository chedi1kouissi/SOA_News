import { Routes } from '@angular/router';
import { HomeComponent } from './features/news/home/home.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { SearchComponent } from './features/news/search/search.component';
import { SavedArticlesComponent } from './features/user/saved-articles/saved-articles.component';

import { PersonalizedNewsComponent } from './features/news/personalized-news/personalized-news.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'search', component: SearchComponent },
    { path: 'saved', component: SavedArticlesComponent },
    { path: 'for-you', component: PersonalizedNewsComponent },
    { path: '**', redirectTo: '' }
];
