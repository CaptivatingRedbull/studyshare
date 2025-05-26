import { useState, useEffect } from 'react';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import {
    Card,
    CardContent,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
    Download,
    UserCircle,
    CalendarDays,
    FileText,
    Users,
    BookOpen,
    Building,
    Star,
    StarHalf,
} from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import { IconFileTypeJpg, IconFileTypePdf, IconFileTypeZip } from '@tabler/icons-react';
import { browseContents } from '@/api/contentApi';
import { Content, ContentCategory, Faculty, Course, Lecturer } from '@/lib/types';
import { Pagination, PaginationContent, PaginationItem, PaginationNext, PaginationPrevious } from '@/components/ui/pagination';
import { getAllFaculties } from '@/api/facultyApi';
import { getAllCourses } from '@/api/courseApi';
import { getAllLecturers } from '@/api/lecturerApi';
import { toastError, toastSuccess } from '@/components/ui/sonner';
import { apiClient } from '@/api/apiClient';
import { Input } from '@/components/ui/input';

const contentCategories: ContentCategory[] = ["PDF", "IMAGE", "ZIP"];

const sortOptions = [
    { value: 'uploadDate', label: 'Hochladedatum' },
    { value: 'title', label: 'Titel (A-Z)' },
    { value: 'rating', label: 'Bewertung' }
];

const GetCategoryIcon = ({ category, className }: { category: ContentCategory, className?: string }) => {
    const defaultClassName = "mr-1 h-4 w-4 shrink-0"; // Minimal margin for icon-text pairing
    const combinedClassName = `${defaultClassName} ${className || ''}`;
    switch (category) {
        case 'PDF': return <IconFileTypePdf className={combinedClassName} />;
        case 'IMAGE': return <IconFileTypeJpg className={combinedClassName} />;
        case 'ZIP': return <IconFileTypeZip className={combinedClassName} />;
        default: return <FileText className={combinedClassName} />;
    }
};

const RenderStars = ({ rating }: { rating?: number }) => {
    const totalStars = 5;
    let displayRating = 0;

    if (rating !== undefined && rating > 0) {
        displayRating = Math.round(rating * 2) / 2;
    }

    const stars = [];
    for (let i = 1; i <= totalStars; i++) {
        if (displayRating === 0) {
            stars.push(<Star key={`star-off-${i}`} className="h-4 w-4 text-muted-foreground/50" />);
        } else if (i <= displayRating) {
            stars.push(<Star key={`star-filled-${i}`} className="h-4 w-4 fill-yellow-400 text-yellow-400" />);
        } else if (i - 0.5 === displayRating) {
            stars.push(<StarHalf key={`star-half-${i}`} className="h-4 w-4 fill-yellow-400 text-yellow-400" />);
        } else {
            stars.push(<Star key={`star-empty-${i}`} className="h-4 w-4 text-yellow-400" />);
        }
    }
    return <div className="flex items-center">{stars}</div>;
};


export function BrowsePage() {
    const [facultyId, setFacultyId] = useState<number | undefined>(undefined);
    const [courseId, setCourseId] = useState<number | undefined>(undefined);
    const [lecturerId, setLecturerId] = useState<number | undefined>(undefined);
    const [category, setCategory] = useState<ContentCategory | undefined>(undefined);
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [sortBy, setSortBy] = useState<"uploadDate" | "title" | "rating">("uploadDate");
    const [sortDirection, setSortDirection] = useState<"desc" | "asc">("desc");
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(12);
    const [contents, setContents] = useState<Content[]>([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [faculties, setFaculties] = useState<Faculty[]>([]);
    const [courses, setCourses] = useState<Course[]>([]);
    const [lecturers, setLecturers] = useState<Lecturer[]>([]);

    useEffect(() => {
        getAllFaculties().then(data => setFaculties(data));
        getAllCourses().then(data => setCourses(data));
        getAllLecturers().then(data => setLecturers(data));
        loadContents();
    }, []);

    useEffect(() => {
        loadContents();
    }, [facultyId, courseId, lecturerId, category, searchTerm, sortBy, sortDirection, currentPage, pageSize]);

    const loadContents = async () => {
        setLoading(true);
        try {
            const result = await browseContents(
                facultyId, courseId, lecturerId, category, searchTerm,
                sortBy, sortDirection, currentPage, pageSize
            );
            setContents(result.content);
            setTotalElements(result.totalElements);
            setTotalPages(result.totalPages);
            setCurrentPage(result.currentPage);
        } catch (error) {
            console.error('Failed to load contents:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFacultyChange = (value: string) => {
        const newFacultyId = value === 'all' ? undefined : Number(value);
        setFacultyId(newFacultyId);
        setCourseId(undefined);
        setLecturerId(undefined);
        setCurrentPage(0);
    };

    const handleCourseChange = (value: string) => {
        const newCourseId = value === 'all' ? undefined : Number(value);
        setCourseId(newCourseId);
        setLecturerId(undefined);
        setCurrentPage(0);
    };

    const handleLecturerChange = (value: string) => {
        const newLecturerId = value === 'all' ? undefined : Number(value);
        setLecturerId(newLecturerId);
        setCurrentPage(0);
    };

    const handleCategoryChange = (value: string) => {
        const newCategory = value === 'all' ? undefined : value as ContentCategory;
        setCategory(newCategory);
        setCurrentPage(0);
    };

    const handleSortChange = (option: { value: string, label: string }) => {
        if (option.value === 'title') {
            setSortBy('title');
            setSortDirection('asc');
        } else if (option.value === 'rating') {
            setSortBy('rating');
            setSortDirection('desc');
        } else {
            setSortBy('uploadDate');
            setSortDirection('desc');
        }
        setCurrentPage(0);
    };

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const handleDownload = async (filePath: string, contentTitle?: string | null) => {
        try {
            const response = await apiClient.get(`/api/contents/download/${filePath}`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            let downloadFilename = filePath;
            if (contentTitle) {
                const extensionMatch = filePath.match(/\.([^.]+)$/);
                const extension = extensionMatch ? extensionMatch[0] : '';
                downloadFilename = `${contentTitle.replace(/[^\w\s.-]/gi, '_')}${extension}`;
            } else {
                downloadFilename = filePath.substring(filePath.lastIndexOf('/') + 1);
            }
            link.setAttribute('download', downloadFilename);
            document.body.appendChild(link);
            link.click();
            link.parentNode?.removeChild(link);
            window.URL.revokeObjectURL(url);
            toastSuccess({ title: "Download gestartet", message: downloadFilename });
        } catch (error) {
            console.error('Download failed:', error);
            toastError({ title: "Download fehlgeschlagen", message: "Datei konnte nicht heruntergeladen werden." });
        }
    };

    const handleRatingClick = (contentId: number) => {
        console.log("Rating clicked for content ID:", contentId);
    };

    return (
        <div className="p-4 md:p-6 space-y-6">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 md:flex md:flex-wrap md:flex-grow gap-4 w-full">
                    <Select value={facultyId?.toString() || 'all'} onValueChange={handleFacultyChange}>
                        <SelectTrigger className="w-full sm:w-auto md:flex-1 min-w-[150px]"><SelectValue placeholder="Fakultät wählen" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Fakultäten</SelectItem>
                            {faculties.map(faculty => (
                                <SelectItem key={faculty.id} value={faculty.id.toString()}>{faculty.name}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                    <Select value={courseId?.toString() || 'all'} onValueChange={handleCourseChange} disabled={!facultyId}>
                        <SelectTrigger className="w-full sm:w-auto md:flex-1 min-w-[150px]"><SelectValue placeholder="Kurs wählen" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Kurse</SelectItem>
                            {courses.filter(course => !facultyId || course.faculty.id === facultyId)
                                .map(course => (<SelectItem key={course.id} value={course.id.toString()}>{course.name}</SelectItem>))}
                        </SelectContent>
                    </Select>
                    <Select value={lecturerId?.toString() || 'all'} onValueChange={handleLecturerChange} disabled={!courseId}>
                        <SelectTrigger className="w-full sm:w-auto md:flex-1 min-w-[150px]"><SelectValue placeholder="Dozent wählen" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Dozenten</SelectItem>
                            {lecturers.filter(lecturer => !courseId || lecturer.courseIds.includes(courseId))
                                .map(lecturer => (<SelectItem key={lecturer.id} value={lecturer.id.toString()}>{lecturer.name}</SelectItem>))}
                        </SelectContent>
                    </Select>
                    <Select value={category || 'all'} onValueChange={handleCategoryChange}>
                        <SelectTrigger className="w-full sm:w-auto md:flex-1 min-w-[150px]"><SelectValue placeholder="Inhaltstyp wählen" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Typen</SelectItem>
                            {contentCategories.map(cat => (<SelectItem key={cat} value={cat}>{cat}</SelectItem>))}
                        </SelectContent>
                    </Select>
                    <Select value={sortBy} onValueChange={(value) => handleSortChange(sortOptions.find(opt => opt.value === value)!)}>
                        <SelectTrigger className="w-full sm:w-auto md:flex-1 min-w-[180px]">
                            <SelectValue placeholder="Sortieren nach" />
                        </SelectTrigger>
                        <SelectContent>
                            {sortOptions.map(option => (
                                <SelectItem key={option.value} value={option.value}>
                                    {option.label}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                    <Input
                        type="text"
                        placeholder="Suche nach Titel, Beschreibung, etc."
                        value={searchTerm}
                        onChange={e => {
                            setSearchTerm(e.target.value);
                            setCurrentPage(0);
                        }}
                        className="w-full sm:w-auto md:flex-1 min-w-[180px]"
                    />
                </div>
            </div>

            <Separator />

            {loading ? (
                <div className="text-center py-10">
                    <p className="text-xl font-semibold text-muted-foreground">Lädt Inhalte...</p>
                </div>
            ) : contents.length > 0 ? (
                <>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {contents.map(item => (
                            <Card key={item.id} className="flex flex-col">
                                <CardHeader>
                                    <CardTitle className="text-lg line-clamp-2 break-words">{item.title}</CardTitle>
                                </CardHeader>
                                <CardContent className="flex-grow content-end">
                                    <div className="flex flex-col items-center text-sm text-muted-foreground flex-wrap gap-y-1">
                                        <div className="flex items-center">
                                            <GetCategoryIcon category={item.contentCategory} />
                                            <span className="truncate">{item.contentCategory}</span>
                                        </div>
                                        <div className="flex items-center">
                                            <CalendarDays className="mr-1 h-4 w-4 shrink-0" />
                                            <span className="truncate">{new Date(item.uploadDate).toLocaleDateString()}</span>
                                        </div>
                                        <button
                                            onClick={() => handleRatingClick(item.id)}
                                            className="flex items-center focus:outline-none focus-visible:ring-2 focus-visible:ring-ring rounded"
                                            aria-label={`Bewertung für ${item.title} ansehen oder abgeben`}
                                        >
                                            <RenderStars rating={item.averageRating} />
                                            {item.averageRating !== undefined && item.averageRating > 0 && (
                                                <span className="ml-1">{item.averageRating.toFixed(1)}</span>
                                            )}
                                        </button>
                                    </div>
                                </CardContent>
                                <CardFooter className="flex flex-col items-center  pt-4 border-t">
                                    <div className="flex flex-col w-full text-xs text-muted-foreground space-y-1">
                                        <div className="flex justify-center ">
                                            <UserCircle className="mr-1.5 h-4 w-4 shrink-0" />
                                            <span className="truncate">{item.uploadedBy?.firstName} {item.uploadedBy?.lastName}</span>
                                        </div>
                                        <div className="flex justify-center">
                                            <Building className="mr-1.5 h-3.5 w-3.5 shrink-0" />
                                            <span className="truncate">{item.faculty?.name || 'N/A'}</span>
                                        </div>
                                        <div className="flex justify-center">
                                            <BookOpen className="mr-1.5 h-3.5 w-3.5 shrink-0" />
                                            <span className="truncate">{item.course?.name || 'N/A'}</span>
                                        </div>
                                        {item.lecturer && (
                                            <div className="flex justify-center">
                                                <Users className="mr-1.5 h-3.5 w-3.5 shrink-0" />
                                                <span className="truncate">{item.lecturer.name}</span>
                                            </div>
                                        )}
                                    </div>
                                    <Button variant="outline" size="sm" className="w-full mt-2"
                                        onClick={() => {
                                            if (item.filePath && typeof item.filePath === 'string' && item.filePath !== "null" && item.filePath !== "undefined") {
                                                handleDownload(item.filePath, item.title);
                                            } else {
                                                toastError({ title: "Download Fehler", message: "Dateipfad ist ungültig oder fehlt." });
                                            }
                                        }}>
                                        <Download className="mr-2 h-4 w-4" />
                                        Herunterladen
                                    </Button>
                                </CardFooter>
                            </Card>
                        ))}
                    </div>

                    {totalPages > 1 && (
                        <div className="mt-6 flex justify-center">
                            <Pagination>
                                <PaginationContent>
                                    <PaginationItem>
                                        <PaginationPrevious
                                            onClick={currentPage === 0 ? undefined : () => handlePageChange(Math.max(0, currentPage - 1))}
                                            aria-disabled={currentPage === 0}
                                        />
                                    </PaginationItem>
                                    {[...Array(totalPages)].map((_, index) => (
                                        <PaginationItem key={index}>
                                            <Button
                                                variant={currentPage === index ? "default" : "outline"}
                                                size="icon"
                                                onClick={() => handlePageChange(index)}
                                            >
                                                {index + 1}
                                            </Button>
                                        </PaginationItem>
                                    ))}
                                    <PaginationItem>
                                        <PaginationNext
                                            onClick={currentPage === totalPages - 1 ? undefined : () => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
                                            aria-disabled={currentPage === totalPages - 1}
                                        />
                                    </PaginationItem>
                                </PaginationContent>
                            </Pagination>
                        </div>
                    )}
                </>
            ) : (
                <div className="text-center py-10">
                    <p className="text-xl font-semibold text-muted-foreground">Keine Inhalte gefunden.</p>
                    <p className="text-sm text-muted-foreground">Versuche andere Filterkriterien.</p>
                </div>
            )}
        </div>
    );
}