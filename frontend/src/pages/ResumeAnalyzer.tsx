import React, { useState } from 'react';
import api from '../lib/api';
import { Upload, FileText, CheckCircle2, XCircle } from 'lucide-react';

export default function ResumeAnalyzer() {
  const [file, setFile] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState('');

  const handleAnalyze = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file || !jobDescription) return;

    setLoading(true);
    setError('');
    
    const formData = new FormData();
    formData.append('file', file);
    formData.append('jobDescription', jobDescription);

    try {
      const response = await api.post('/resume/analyze', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setResult(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to analyze resume');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
      {/* Input Section */}
      <div className="glass-panel p-6">
        <h2 className="text-2xl font-bold mb-6 flex items-center">
          <FileText className="mr-2 text-primary" /> Smart Resume Analyzer
        </h2>

        {error && (
          <div className="bg-danger/20 text-danger border border-danger/30 p-4 rounded-lg mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleAnalyze} className="space-y-6">
          {/* Upload Dropzone */}
          <div className="border-2 border-dashed border-white/20 rounded-xl p-8 text-center hover:bg-white/5 hover:border-primary/50 transition-all cursor-pointer relative">
            <input 
              type="file" 
              accept=".pdf"
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              required
            />
            <div className="flex flex-col items-center">
              <Upload className={`w-12 h-12 mb-4 ${file ? 'text-success' : 'text-primary'}`} />
              {file ? (
                <div>
                  <p className="font-medium text-success">{file.name}</p>
                  <p className="text-sm text-gray-400 mt-1">Click to change file</p>
                </div>
              ) : (
                <div>
                  <p className="font-medium">Drag & drop your resume PDF</p>
                  <p className="text-sm text-gray-400 mt-1">or click to browse</p>
                </div>
              )}
            </div>
          </div>

          <div>
            <label className="block font-medium mb-2">Job Description</label>
            <textarea
              className="input-field h-40 resize-none"
              placeholder="Paste the target job description here..."
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-primary w-full py-3" disabled={loading || !file || !jobDescription}>
            {loading ? 'Analyzing with AI...' : 'Analyze Match'}
          </button>
        </form>
      </div>

      {/* Results Section */}
      <div className="space-y-6">
        {result ? (
          <>
            <div className="glass-panel p-6 flex items-center justify-between">
              <div>
                <h3 className="text-xl font-bold">Match Score</h3>
                <p className="text-gray-400 text-sm mt-1">Based on keyword extraction</p>
              </div>
              <div className="relative h-24 w-24">
                <svg className="w-full h-full transform -rotate-90">
                  <circle cx="48" cy="48" r="40" fill="none" stroke="currentColor" strokeWidth="8" className="text-secondary" />
                  <circle 
                    cx="48" cy="48" r="40" fill="none" stroke="currentColor" strokeWidth="8" 
                    className={result.matchPercentage >= 70 ? 'text-success' : result.matchPercentage >= 40 ? 'text-warning' : 'text-danger'}
                    strokeDasharray={`${result.matchPercentage * 2.51} 251`}
                    strokeLinecap="round"
                  />
                </svg>
                <div className="absolute inset-0 flex flex-col items-center justify-center">
                  <span className="text-xl font-bold">{result.matchPercentage}%</span>
                </div>
              </div>
            </div>

            <div className="glass-panel p-6">
              <h3 className="font-semibold mb-4 flex items-center">
                <CheckCircle2 className="w-5 h-5 text-success mr-2" /> Matching Skills Found
              </h3>
              <div className="flex flex-wrap gap-2">
                {result.foundSkills?.length > 0 ? (
                  result.foundSkills.map((skill: string) => (
                    <span key={skill} className="bg-success/20 text-success border border-success/30 px-3 py-1 rounded-full text-sm capitalize">
                      {skill}
                    </span>
                  ))
                ) : (
                  <span className="text-gray-500 italic">No exact matches found.</span>
                )}
              </div>
            </div>

            <div className="glass-panel p-6">
              <h3 className="font-semibold mb-4 flex items-center">
                <XCircle className="w-5 h-5 text-danger mr-2" /> Missing Keywords
              </h3>
              <div className="flex flex-wrap gap-2">
                {result.missingSkills?.length > 0 ? (
                  result.missingSkills.map((skill: string) => (
                    <span key={skill} className="bg-danger/20 text-danger border border-danger/30 px-3 py-1 rounded-full text-sm capitalize">
                      {skill}
                    </span>
                  ))
                ) : (
                  <span className="text-gray-500 italic">Your resume has all the required skills!</span>
                )}
              </div>
            </div>
          </>
        ) : (
          <div className="glass-panel h-full flex flex-col items-center justify-center p-12 text-center opacity-50">
            <FileText className="w-16 h-16 mb-4 text-gray-500" />
            <h3 className="text-xl font-medium mb-2">Awaiting Input</h3>
            <p className="text-gray-400">Upload your resume and paste a job description to see how well you match the role.</p>
          </div>
        )}
      </div>
    </div>
  );
}
