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
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
    ArrowUpDown,
    Download,
    UserCircle,
    CalendarDays,
    FileText,
    Users,
    BookOpen,
    Building
} from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import { IconFileTypeJpg, IconFileTypePdf, IconFileTypeZip } from '@tabler/icons-react';
import { browseContents } from '@/api/contentApi';
import { Content, ContentCategory, Faculty, Course, Lecturer } from '@/lib/types';
import { Pagination } from '@/components/ui/pagination';
import { PaginationContent, PaginationItem, PaginationNext, PaginationPrevious } from '@/components/ui/pagination';
import { getAllFaculties } from '@/api/facultyApi';
import { getAllCourses } from '@/api/courseApi';
import { getAllLecturers } from '@/api/lecturerApi';

const contentCategories: ContentCategory[] = ["PDF", "IMAGE", "ZIP"];

const sortOptions = [
    { value: 'uploadDate', label: 'Datum (neu zuerst)' },
    { value: 'title', label: 'Titel (A-Z)' }
];

// --- Helper function to get category icon ---
const GetCategoryIcon = ({ category, className }: { category: ContentCategory, className?: string }) => {
    const defaultClassName = "mr-2 h-4 w-4";
    const combinedClassName = `${defaultClassName} ${className || ''}`;
    switch (category) {
        case 'PDF': return <IconFileTypePdf className={combinedClassName} />;
        case 'IMAGE': return <IconFileTypeJpg className={combinedClassName} />;
        case 'ZIP': return <IconFileTypeZip className={combinedClassName} />;
        default: return <FileText className={combinedClassName} />;
    }
};

export function BrowsePage() {
    // State for filters
    const [facultyId, setFacultyId] = useState<number | undefined>(undefined);
    const [courseId, setCourseId] = useState<number | undefined>(undefined);
    const [lecturerId, setLecturerId] = useState<number | undefined>(undefined);
    const [category, setCategory] = useState<ContentCategory | undefined>(undefined);
    const [searchTerm, setSearchTerm] = useState<string>('');

    // State for sorting
    const [sortBy, setSortBy] = useState<"uploadDate" | "title">("uploadDate");
    const [sortDirection, setSortDirection] = useState<"desc" | "asc">("desc");

    // State for pagination
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(12);

    // State for data
    const [contents, setContents] = useState<Content[]>([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);

    // Data for filter options
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
                facultyId,
                courseId,
                lecturerId,
                category,
                searchTerm,
                sortBy,
                sortDirection,
                currentPage,
                pageSize
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
        } else {
            setSortBy('uploadDate');
            setSortDirection('desc');
        }
        setCurrentPage(0);
    };

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    return (
        <div className="p-4 md:p-6 space-y-6">
            {/* Filter Section */}
            <div className="flex flex-col md:flex-row gap-4 items-center">
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 w-full">
                    <Select
                        value={facultyId?.toString() || 'all'}
                        onValueChange={handleFacultyChange}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Fakultät wählen" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Fakultäten</SelectItem>
                            {faculties.map(faculty => (
                                <SelectItem key={faculty.id} value={faculty.id.toString()}>{faculty.name}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <Select
                        value={courseId?.toString() || 'all'}
                        onValueChange={handleCourseChange}
                        disabled={!facultyId}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Kurs wählen" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Kurse</SelectItem>
                            {courses
                                .filter(course => !facultyId || course.faculty.id === facultyId)
                                .map(course => (
                                    <SelectItem key={course.id} value={course.id.toString()}>{course.name}</SelectItem>
                                ))
                            }
                        </SelectContent>
                    </Select>

                    <Select
                        value={lecturerId?.toString() || 'all'}
                        onValueChange={handleLecturerChange}
                        disabled={!courseId}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Dozent wählen" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Dozenten</SelectItem>
                            {lecturers
                                .filter(lecturer => !courseId || lecturer.courseIds.includes(courseId))
                                .map(lecturer => (
                                    <SelectItem key={lecturer.id} value={lecturer.id.toString()}>{lecturer.name}</SelectItem>
                                ))
                            }
                        </SelectContent>
                    </Select>

                    <Select
                        value={category || 'all'}
                        onValueChange={handleCategoryChange}
                    >
                        <SelectTrigger>
                            <SelectValue placeholder="Inhaltstyp wählen" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Alle Typen</SelectItem>
                            {contentCategories.map(cat => (
                                <SelectItem key={cat} value={cat}>{cat}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </div>

                <div className="md:ml-auto flex-shrink-0">
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="outline">
                                <ArrowUpDown className="mr-2 h-4 w-4" />
                                Sortieren nach: {sortBy === 'uploadDate' ? 'Datum' : 'Titel'}
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                            {sortOptions.map((option) => (
                                <DropdownMenuItem
                                    key={option.value}
                                    onClick={() => handleSortChange(option)}
                                >
                                    {option.label}
                                </DropdownMenuItem>
                            ))}
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>
            </div>

            <Separator />

            {/* Content Display Section */}
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
                                    <CardTitle className="text-lg line-clamp-2">{item.title}</CardTitle>
                                    <div className="flex items-center text-sm text-muted-foreground pt-1">
                                        <GetCategoryIcon category={item.contentCategory} />
                                        <span>{item.contentCategory}</span>
                                        <span className="mx-1.5">·</span>
                                        <CalendarDays className="mr-1.5 h-4 w-4" />
                                        <span>{new Date(item.uploadDate).toLocaleDateString()}</span>
                                    </div>
                                </CardHeader>
                                <CardContent className="flex-grow">
                                    <p className="text-sm text-muted-foreground line-clampw-3">{item.title}</p>
                                </CardContent>
                                <CardFooter className="flex flex-col items-start space-y-2 pt-4 border-t">
                                    <div className="flex justify-between w-full text-xs text-muted-foreground">
                                        <div className="flex items-center">
                                            <UserCircle className="mr-1.5 h-4 w-4" />
                                            <span>{item.uploadedBy?.firstName} {item.uploadedBy?.lastName}</span>
                                        </div>
                                        
                                    </div>
                                    <div className="w-full space-y-1 text-xs text-muted-foreground">
                                        <div className="flex items-center">
                                            <Building className="mr-1.5 h-3.5 w-3.5" />
                                            <span>{item.faculty?.name || 'N/A'}</span>
                                        </div>
                                        <div className="flex items-center">
                                            <BookOpen className="mr-1.5 h-3.5 w-3.5" />
                                            <span>{item.course?.name || 'N/A'}</span>
                                        </div>
                                        {item.lecturer && (
                                            <div className="flex items-center">
                                                <Users className="mr-1.5 h-3.5 w-3.5" />
                                            </div>
                                        )}
                                    </div>
                                    <Button variant="outline" size="sm" className="w-full mt-2">
                                        <Download className="mr-2 h-4 w-4" />
                                        Herunterladen
                                    </Button>
                                </CardFooter>
                            </Card>
                        ))}
                    </div>

                    {/* Pagination */}
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