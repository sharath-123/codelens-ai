import { useEffect, useState } from "react";
import axios from "axios";
import ReactMarkdown from "react-markdown";
import "./App.css";

type ReviewResponse = {
    id: string;
    userId: string;
    language: string;
    status: string;
    qualityScore: number | null;
    summary: string | null;
    createdAt: string;
    completedAt: string | null;
};

type Finding = {
    id: string;
    type: string;
    severity: string;
    title: string;
    description: string;
    recommendation: string;
    lineNumber: number | null;
};

function App() {
    const [language, setLanguage] = useState("Java");
    const [sourceCode, setSourceCode] = useState("");

    const [review, setReview] = useState<ReviewResponse | null>(null);
    const [findings, setFindings] = useState<Finding[]>([]);

    const [history, setHistory] = useState<ReviewResponse[]>([]);

    const [loading, setLoading] = useState(false);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [error, setError] = useState("");

    const loadHistory = async () => {
        setHistoryLoading(true);

        try {
            const response = await axios.get<ReviewResponse[]>(
                "/api/v1/reviews"
            );

            const sortedHistory = [...response.data].sort(
                (a, b) =>
                    new Date(b.createdAt).getTime() -
                    new Date(a.createdAt).getTime()
            );

            setHistory(sortedHistory);
        } catch (err) {
            console.error("Failed to load review history:", err);
        } finally {
            setHistoryLoading(false);
        }
    };

    useEffect(() => {
        loadHistory();
    }, []);

    const loadReview = async (reviewId: string) => {
        setError("");

        try {
            const reviewResponse = await axios.get<ReviewResponse>(
                `/api/v1/reviews/${reviewId}`
            );

            setReview(reviewResponse.data);

            const findingsResponse = await axios.get<Finding[]>(
                `/api/v1/reviews/${reviewId}/findings`
            );

            setFindings(findingsResponse.data);

            window.scrollTo({
                top: 0,
                behavior: "smooth",
            });
        } catch (err) {
            console.error("Failed to load review:", err);

            setError(
                "Unable to load this review. Please try again."
            );
        }
    };

    const analyzeCode = async () => {
        if (!sourceCode.trim()) {
            return;
        }

        setLoading(true);
        setError("");
        setReview(null);
        setFindings([]);

        try {
            const response = await axios.post<ReviewResponse>(
                "/api/v1/reviews",
                {
                    language,
                    sourceCode,
                },
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            const reviewData = response.data;

            setReview(reviewData);

            const findingsResponse = await axios.get<Finding[]>(
                `/api/v1/reviews/${reviewData.id}/findings`
            );

            setFindings(findingsResponse.data);

            setHistory((currentHistory) => {
                const updatedHistory = [
                    reviewData,
                    ...currentHistory.filter(
                        (item) => item.id !== reviewData.id
                    ),
                ];

                return updatedHistory.sort(
                    (a, b) =>
                        new Date(b.createdAt).getTime() -
                        new Date(a.createdAt).getTime()
                );
            });
        } catch (err) {
            console.error("Code review failed:", err);

            setError(
                "Unable to analyze the code. Please check that the backend is available."
            );
        } finally {
            setLoading(false);
        }
    };

    const getFindingIcon = (type: string) => {
        switch (type) {
            case "SECURITY":
                return "🔐";
            case "PERFORMANCE":
                return "⚡";
            case "MAINTAINABILITY":
                return "🛠";
            case "ARCHITECTURE":
                return "🏗";
            case "BUG":
                return "🐞";
            case "FORMATTING":
                return "📝";
            default:
                return "🔎";
        }
    };

    const getSeverityClass = (severity: string) => {
        return severity.toLowerCase();
    };

    const formatDate = (date: string) => {
        return new Date(date).toLocaleString();
    };

    return (
        <div className="app">
            <header className="topbar">
                <div className="brand">
                    <div className="brand-icon">C</div>

                    <div>
                        <h1>CodeLens AI</h1>
                        <span>Intelligent Code Reviewer</span>
                    </div>
                </div>

                <div className="header-status">
                    <span className="status-dot"></span>
                    AI Review Engine
                </div>
            </header>

            <main className="container">
                {/* HERO */}

                <section className="hero">
                    <p className="eyebrow">
                        AI-POWERED CODE ANALYSIS
                    </p>

                    <h2>
                        Review your code with confidence.
                    </h2>

                    <p className="hero-text">
                        Detect bugs, security vulnerabilities,
                        performance issues and architectural problems
                        with AI-powered code review.
                    </p>
                </section>

                {/* CODE SUBMISSION */}

                <section className="review-card">
                    <div className="section-header">
                        <div>
                            <h3>Submit Code</h3>

                            <p>
                                Paste your source code below to start an
                                intelligent review.
                            </p>
                        </div>

                        <div className="language-wrapper">
                            <label htmlFor="language">
                                Language
                            </label>

                            <select
                                id="language"
                                value={language}
                                onChange={(event) =>
                                    setLanguage(event.target.value)
                                }
                            >
                                <option>Java</option>
                                <option>JavaScript</option>
                                <option>TypeScript</option>
                                <option>Python</option>
                                <option>SQL</option>
                            </select>
                        </div>
                    </div>

                    <textarea
                        className="code-editor"
                        placeholder="Paste your source code here..."
                        value={sourceCode}
                        onChange={(event) =>
                            setSourceCode(event.target.value)
                        }
                        spellCheck={false}
                    />

                    <div className="action-row">
            <span className="character-count">
              {sourceCode.length} characters
            </span>

                        <button
                            className="analyze-button"
                            onClick={analyzeCode}
                            disabled={
                                !sourceCode.trim() || loading
                            }
                        >
                            {loading
                                ? "Analyzing..."
                                : "Analyze Code"}
                        </button>
                    </div>

                    {loading && (
                        <div className="loading-message">
                            <span className="loading-spinner"></span>

                            CodeLens AI is analyzing your code
                            with Gemini...
                        </div>
                    )}

                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}
                </section>

                {/* REVIEW RESULT */}

                {review && (
                    <section className="result-section">
                        <div className="result-header">
                            <div>
                                <p className="eyebrow">
                                    LATEST REVIEW
                                </p>

                                <h3>Review Result</h3>
                            </div>

                            <span className="completed-badge">
                {review.status}
              </span>
                        </div>

                        {/* SCORE */}

                        <div className="score-card">
                            <div>
                <span className="metric-label">
                  Quality Score
                </span>

                                <div className="score">
                                    {review.qualityScore ?? "—"}

                                    <span>/10</span>
                                </div>
                            </div>

                            <div className="score-description">
                                <strong>
                                    CodeLens Analysis
                                </strong>

                                <p>
                                    Deterministic code analysis combined
                                    with AI-powered reasoning and
                                    historical review guidance.
                                </p>
                            </div>
                        </div>

                        {/* GEMINI REVIEW */}

                        <div className="ai-review-card">
                            <div className="card-title">
                <span className="title-icon">
                  ✦
                </span>

                                <h4>AI Review</h4>

                                <span className="gemini-label">
                  Gemini
                </span>
                            </div>

                            <div className="ai-review-content">
                                <ReactMarkdown>
                                    {review.summary || ""}
                                </ReactMarkdown>
                            </div>
                        </div>

                        {/* FINDINGS */}

                        <div className="findings-header">
                            <div>
                                <h3>Findings</h3>

                                <p>
                                    Issues identified by the CodeLens
                                    analysis engine.
                                </p>
                            </div>
                        </div>

                        {findings.length === 0 ? (
                            <div className="no-findings">
                                No deterministic findings were
                                detected.
                            </div>
                        ) : (
                            <div className="findings-list">
                                {findings.map((finding) => (
                                    <div
                                        className="finding-card"
                                        key={finding.id}
                                    >
                                        <div className="finding-top">
                      <span className="finding-icon">
                        {getFindingIcon(
                            finding.type
                        )}
                      </span>

                                            <span
                                                className={`severity ${getSeverityClass(
                                                    finding.severity
                                                )}`}
                                            >
                        {finding.severity}
                      </span>
                                        </div>

                                        <div className="finding-category">
                                            {finding.type}
                                        </div>

                                        <h4>
                                            {finding.title}
                                        </h4>

                                        <p>
                                            {finding.description}
                                        </p>

                                        <div className="recommendation">
                                            <strong>
                                                Recommendation
                                            </strong>

                                            <span>
                        {finding.recommendation}
                      </span>
                                        </div>

                                        {finding.lineNumber && (
                                            <div className="line-number">
                                                Line {finding.lineNumber}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                    </section>
                )}

                {/* REVIEW HISTORY */}

                <section className="history-section">
                    <div className="history-header">
                        <div>
                            <p className="eyebrow">
                                PERSISTENT HISTORY
                            </p>

                            <h3>Review History</h3>

                            <p>
                                Track your previous code reviews and
                                quality scores.
                            </p>
                        </div>

                        <button
                            className="refresh-button"
                            onClick={loadHistory}
                            disabled={historyLoading}
                        >
                            {historyLoading
                                ? "Loading..."
                                : "Refresh"}
                        </button>
                    </div>

                    <div className="history-card">
                        {history.length === 0 ? (
                            <div className="empty-history">
                                {historyLoading
                                    ? "Loading review history..."
                                    : "No previous reviews found."}
                            </div>
                        ) : (
                            history.map((item) => (
                                <div
                                    className="history-item"
                                    key={item.id}
                                >
                                    <div className="history-language">
                                        <div className="history-language-icon">
                                            {item.language
                                                .charAt(0)
                                                .toUpperCase()}
                                        </div>

                                        <div>
                                            <strong>
                                                {item.language}
                                            </strong>

                                            <span>
                        {formatDate(
                            item.createdAt
                        )}
                      </span>
                                        </div>
                                    </div>

                                    <div className="history-score">
                                        <span>Score</span>

                                        <strong>
                                            {item.qualityScore ?? "—"}
                                            /10
                                        </strong>
                                    </div>

                                    <span className="history-status">
                    {item.status}
                  </span>

                                    <button
                                        className="view-button"
                                        onClick={() =>
                                            loadReview(item.id)
                                        }
                                    >
                                        View Review →
                                    </button>
                                </div>
                            ))
                        )}
                    </div>
                </section>
            </main>
        </div>
    );
}

export default App;