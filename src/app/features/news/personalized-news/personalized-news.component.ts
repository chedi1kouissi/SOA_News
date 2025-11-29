import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ArticleCardComponent } from '../../../shared/components/article-card/article-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
    selector: 'app-personalized-news',
    standalone: true,
    imports: [CommonModule, ArticleCardComponent, LoadingSpinnerComponent],
    templateUrl: './personalized-news.component.html',
    styleUrl: './personalized-news.component.css'
})
export class PersonalizedNewsComponent implements OnInit {
    articles: any[] = [];
    loading: boolean = false;
    currentUser: any = null;
    savedArticleUrls: Set<string> = new Set();

    constructor(
        private userService: UserService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.authService.currentUser$.subscribe(user => {
            this.currentUser = user;
            if (user) {
                this.loadPersonalizedNews();
                this.loadSavedArticles();
            }
        });
    }

    loadPersonalizedNews() {
        this.loading = true;
        this.userService.getPersonalizedNews(this.currentUser.id).subscribe({
            next: (data) => {
                this.articles = data.articles || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading personalized news', err);
                this.loading = false;
            }
        });
    }

    loadSavedArticles() {
        this.userService.getSavedArticles(this.currentUser.id).subscribe({
            next: (articles) => {
                this.savedArticleUrls = new Set(articles.map(a => a.url));
            }
        });
    }

    onSaveArticle(article: any) {
        if (!this.currentUser) return;
        this.userService.saveArticle(this.currentUser.id, {
            article: article,
            category: 'general', // Context is mixed, defaulting
            country: 'us'
        }).subscribe(() => {
            this.savedArticleUrls.add(article.url);
        });
    }

    onUnsaveArticle(article: any) {
        if (!this.currentUser) return;
        this.userService.unsaveArticle(this.currentUser.id, article.url).subscribe(() => {
            this.savedArticleUrls.delete(article.url);
        });
    }

    isArticleSaved(article: any): boolean {
        return this.savedArticleUrls.has(article.url);
    }
}
