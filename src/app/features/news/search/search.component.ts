import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NewsService } from '../../../core/services/news.service';
import { CommonModule } from '@angular/common';
import { ArticleCardComponent } from '../../../shared/components/article-card/article-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-search',
    standalone: true,
    imports: [CommonModule, ArticleCardComponent, LoadingSpinnerComponent],
    templateUrl: './search.component.html',
    styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
    query: string = '';
    articles: any[] = [];
    loading: boolean = false;
    currentUser: any = null;
    savedArticleUrls: Set<string> = new Set();

    constructor(
        private route: ActivatedRoute,
        private newsService: NewsService,
        private userService: UserService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.authService.currentUser$.subscribe(user => {
            this.currentUser = user;
            if (user) {
                this.loadSavedArticles();
            }
        });

        this.route.queryParams.subscribe(params => {
            this.query = params['q'];
            if (this.query) {
                this.searchNews();
            }
        });
    }

    searchNews() {
        this.loading = true;
        this.newsService.searchNews(this.query).subscribe({
            next: (data) => {
                this.articles = data.articles || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Error searching news', err);
                this.loading = false;
            }
        });
    }

    loadSavedArticles() {
        if (!this.currentUser) return;
        this.userService.getSavedArticles(this.currentUser.id).subscribe({
            next: (articles) => {
                this.savedArticleUrls = new Set(articles.map(a => a.url));
            }
        });
    }

    onSaveArticle(article: any) {
        if (!this.currentUser) return; // Should handle login redirect or alert
        this.userService.saveArticle(this.currentUser.id, {
            articleUrl: article.url,
            category: 'general', // Search doesn't have a category context easily, defaulting
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
